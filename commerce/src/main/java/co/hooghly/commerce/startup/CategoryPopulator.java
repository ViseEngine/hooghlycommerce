package co.hooghly.commerce.startup;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.MessageResourceService;
import co.hooghly.commerce.business.ProductTypeService;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.domain.Category;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.MerchantStoreView;
import co.hooghly.commerce.domain.MessageResource;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(10)
public class CategoryPopulator extends AbstractDataPopulator {

	@Value("classpath:demo-data/category_*.txt")
	private Resource[] resources;

	public CategoryPopulator() {
		super("CATEGORY");
	}

	@Autowired
	protected CategoryService categoryService;

	@Autowired
	protected ProductTypeService productTypeService;

	@Autowired
	protected LanguageService languageService;

	@Autowired
	protected CountryService countryService;

	@Autowired
	protected ZoneService zoneService;

	@Autowired
	protected MerchantStoreService merchantService;
	
	@Autowired
	private MessageResourceService messageResourceService;
	
	

	public List<String> getFileContent(URI fileName) {
		List<String> list = new ArrayList<>();

		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {

			list = stream.filter(i -> StringUtils.isNotBlank(i) && !StringUtils.startsWith(i, "#")).collect(Collectors.toList());

		} catch (Exception e) {
			log.error("Error ", e);
		}

		return list;

	}

	@Override
	public void runInternal(String... args) throws Exception {
		log.info("10. Populating categories.");
		
		
		// create a merchant
		MerchantStore store = merchantService.getMerchantStore(MerchantStore.DEFAULT_STORE);
		MerchantStoreView storeViewDefaultEn = store.getStoreViews().stream().filter(i -> i.isDefaultView()).findFirst().orElse(null);
		MerchantStoreView storeView = store.getStoreViews().stream().filter(i -> !i.isDefaultView()).findFirst().orElse(null);
		
		for (Resource r : resources) {
			log.debug("Found resource - {}", r.getFilename());

			List<String> contents = getFileContent(r.getURI());
			int parentSortOder = 0;
			int childSortOrder = 0;
			Category parent = null;
			for (String s : contents) {
				
				log.debug("Line - {}", s);
				int i = s.indexOf('|');
				Category category = new Category();
				
				String engPart = s.substring(0, i);
				String hiPart = s.substring(i+1);
				
				String code = StringUtils.lowerCase(StringUtils.replace(StringUtils.trim(engPart), " ", "-"));
				
				log.debug("Code - {}", code);
				
				if (StringUtils.startsWith(engPart, " ")) {
					
					// child entry
					log.debug("Child category - {}", s);
					category.setMerchantStore(store);
					category.setCode(code);
					category.setVisible(true);
					category.setSortOrder(childSortOrder++);
					category.setParent(parent);
					category.setSeUrl("/categories/"+code);
					category.setName("msg.category."+code);
					
					categoryService.create(category);
				} else {
					category.setMerchantStore(store);
					category.setCode(code);
					category.setVisible(true);
					category.setSortOrder(parentSortOder++);
					category.setName("msg.category."+code);
					category.setSeUrl("/categories/"+code);
					
					categoryService.create(category);
					parent = category;
					childSortOrder = 0;
				}
				
				//will create message resources as these are throw-away demo data.
				MessageResource mr = new MessageResource();
				mr.setDomain("Category");
				mr.setLocale(storeViewDefaultEn.computeLocale().toString());
				mr.setMessageKey(category.getName());
				mr.setMessageText(engPart);
				
				MessageResource mrHi = new MessageResource();
				mrHi.setDomain("Category");
				mrHi.setLocale(storeView.computeLocale().toString());
				mrHi.setMessageKey(category.getName());
				mrHi.setMessageText(hiPart);
				
				messageResourceService.save(mr);
				messageResourceService.save(mrHi);
				
			}
		}


	}

	

	

}
