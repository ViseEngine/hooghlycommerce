package co.hooghly.commerce.startup;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.ProductTypeService;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.CategoryDescription;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(10)
public class CategoryPopulator extends AbstractDataPopulator {
	
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

	@Override
	public void runInternal(String... args) throws Exception {
		log.info("10. Populating categories.");
		
		//2 languages by default
		Language en = languageService.getByCode("en");
		Language hi = languageService.getByCode("hi");
		
		
		//create a merchant
		MerchantStore store = merchantService.getMerchantStore(MerchantStore.DEFAULT_STORE);
		
		Category mobile = new Category();
		mobile.setMerchantStore(store);
		mobile.setCode("mobile");
		mobile.setVisible(true);

	    CategoryDescription mobileEnglishDescription = new CategoryDescription();
	    mobileEnglishDescription.setName("Mobile");
	    mobileEnglishDescription.setCategory(mobile);
	    mobileEnglishDescription.setLanguage(en);
	    mobileEnglishDescription.setSeUrl("mobile");

	    CategoryDescription mobileFrenchDescription = new CategoryDescription();
	    mobileFrenchDescription.setName("Mobile");
	    mobileFrenchDescription.setCategory(mobile);
	    mobileFrenchDescription.setLanguage(hi);
	    mobileFrenchDescription.setSeUrl("mobile");

	    List<CategoryDescription> descriptions = new ArrayList<CategoryDescription>();
	    descriptions.add(mobileEnglishDescription);
	    descriptions.add(mobileFrenchDescription);

	    mobile.setDescriptions(descriptions);
	    
	   

	    categoryService.create(mobile);
	    
	    addChildren(mobile, store, en, hi);

	    Category accessories = new Category();
	    accessories.setMerchantStore(store);
	    accessories.setCode("accessories");
	    accessories.setVisible(false);

	    CategoryDescription accessoriesEnglishDescription = new CategoryDescription();
	    accessoriesEnglishDescription.setName("Accessories");
	    accessoriesEnglishDescription.setCategory(accessories);
	    accessoriesEnglishDescription.setLanguage(en);
	    accessoriesEnglishDescription.setSeUrl("accessories");

	    CategoryDescription accessoriesFrenchDescription = new CategoryDescription();
	    accessoriesFrenchDescription.setName("Accessoires");
	    accessoriesFrenchDescription.setCategory(accessories);
	    accessoriesFrenchDescription.setLanguage(hi);
	    accessoriesFrenchDescription.setSeUrl("accessoires");

	    List<CategoryDescription> descriptions2 = new ArrayList<CategoryDescription>();
	    descriptions2.add(accessoriesEnglishDescription);
	    descriptions2.add(accessoriesFrenchDescription);

	    accessories.setDescriptions(descriptions2);

	    categoryService.create(accessories);
	    
	    Category handicrafts = new Category();
	    handicrafts.setMerchantStore(store);
	    handicrafts.setCode("handicrafts");

	    CategoryDescription handicraftsEnglishDescription = new CategoryDescription();
	    handicraftsEnglishDescription.setName("Handicrafts");
	    handicraftsEnglishDescription.setCategory(handicrafts);
	    handicraftsEnglishDescription.setLanguage(en);
	    handicraftsEnglishDescription.setSeUrl("handicrafts");

	    CategoryDescription handicraftsFrenchDescription = new CategoryDescription();
	    handicraftsFrenchDescription.setName("Artisanats");
	    handicraftsFrenchDescription.setCategory(handicrafts);
	    handicraftsFrenchDescription.setLanguage(hi);
	    handicraftsFrenchDescription.setSeUrl("artisanats");

	    List<CategoryDescription> descriptions4 = new ArrayList<CategoryDescription>();
	    descriptions4.add(handicraftsEnglishDescription);
	    descriptions4.add(handicraftsFrenchDescription);

	    handicrafts.setDescriptions(descriptions4);
	    
	   

	    categoryService.create(handicrafts);
	    
	    
	}

	private void addChildren(Category mobile, MerchantStore store, Language en, Language fr) {
		//1. Samsung
		Category samsung = new Category();
		samsung.setMerchantStore(store);
		samsung.setCode("samsung");
		samsung.setVisible(true);

	    CategoryDescription samsungEnglishDescription = new CategoryDescription();
	    samsungEnglishDescription.setName("Samsung");
	    samsungEnglishDescription.setCategory(samsung);
	    samsungEnglishDescription.setLanguage(en);
	    samsungEnglishDescription.setSeUrl("samsung-mobile");

	    CategoryDescription samsungFrenchDescription = new CategoryDescription();
	    samsungFrenchDescription.setName("Samsung");
	    samsungFrenchDescription.setCategory(samsung);
	    samsungFrenchDescription.setLanguage(fr);
	    samsungFrenchDescription.setSeUrl("samsung-mobile-fr");

	    List<CategoryDescription> descriptions = new ArrayList<CategoryDescription>();
	    descriptions.add(samsungEnglishDescription);
	    descriptions.add(samsungFrenchDescription);

	    samsung.setDescriptions(descriptions);
	    
	    samsung.setParent(mobile);
	    mobile.getCategories().add(samsung);
	    
	    categoryService.create(samsung);

	    //2.LG
	    Category lg = new Category();
	    lg.setMerchantStore(store);
	    lg.setCode("LG");
	    lg.setVisible(true);

	    CategoryDescription lgEnglishDescription = new CategoryDescription();
	    lgEnglishDescription.setName("LG");
	    lgEnglishDescription.setCategory(lg);
	    lgEnglishDescription.setLanguage(en);
	    lgEnglishDescription.setSeUrl("lg-mobile");

	    CategoryDescription lgFrenchDescription = new CategoryDescription();
	    lgFrenchDescription.setName("LG");
	    lgFrenchDescription.setCategory(lg);
	    lgFrenchDescription.setLanguage(fr);
	    lgFrenchDescription.setSeUrl("lg-mobile-fr");

	    descriptions = new ArrayList<CategoryDescription>();
	    descriptions.add(lgEnglishDescription);
	    descriptions.add(lgFrenchDescription);

	    lg.setDescriptions(descriptions);
	    
	    lg.setParent(mobile);
	    mobile.getCategories().add(lg);
	    
	    categoryService.create(lg);
		
	}

}
