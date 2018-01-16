package co.hooghly.commerce.startup;

import java.util.List;

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
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.MessageResource;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(9)
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

	@Override
	public void runInternal(String... args) throws Exception {
		log.info("9. Populating categories.");

		// create a merchant
		MerchantStore store = merchantService.getMerchantStore(MerchantStore.DEFAULT_STORE);

		for (Resource r : resources) {
			log.debug("Found resource - {}", r.getFilename());

			List<String> contents = getFileContent(r.getInputStream());
			int parentSortOder = 0;
			int childSortOrder = 0;
			Category parent = null;
			for (String s : contents) {

				log.debug("Line - {}", s);
				int i = s.indexOf('|');
				Category category = new Category();

				String engPart = s.substring(0, i);
				String hiPart = s.substring(i + 1);

				String[] parts = new String[] { engPart, hiPart };

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
					category.setSeUrl("/categories/" + code);
					category.setName("msg.category." + code);

					categoryService.create(category);
				} else {
					category.setMerchantStore(store);
					category.setCode(code);
					category.setVisible(true);
					category.setSortOrder(parentSortOder++);
					category.setName("msg.category." + code);
					category.setSeUrl("/categories/" + code);

					categoryService.create(category);
					parent = category;
					childSortOrder = 0;
				}

				int k = 0;
				for (Language lang : store.getLanguages()) {
					MessageResource mr = new MessageResource();
					mr.setDomain("Category");
					mr.setLocale(lang.computeLocale(store.getCountry()).toString());
					mr.setMessageKey(category.getName());
					mr.setMessageText(parts[k++]);

					messageResourceService.save(mr);
				}

			}
		}

	}

}
