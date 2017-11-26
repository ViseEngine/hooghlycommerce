package co.hooghly.commerce.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.ManufacturerService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Manufacturer;
import co.hooghly.commerce.domain.ManufacturerDescription;
import co.hooghly.commerce.domain.MerchantStore;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(11)
public class ManufacturerPopulator extends AbstractDataPopulator{
	
	public ManufacturerPopulator() {
		super("MANUFACTURER");
	}
	
	@Autowired
	protected ManufacturerService manufacturerService;
	
	@Autowired
	protected MerchantStoreService merchantService;
	
	@Autowired
	protected LanguageService languageService;

	@Override
	public void runInternal(String... args) throws Exception {
		
		
		log.info("11.Populating manufacturer.");
		
		MerchantStore store = merchantService.getMerchantStore(MerchantStore.DEFAULT_STORE);
		
		Language en = languageService.getByCode("en");
		
		
		Manufacturer samsung = new Manufacturer();
		samsung.setMerchantStore(store);
		samsung.setCode("samsung");

	    ManufacturerDescription samsungd = new ManufacturerDescription();
	    samsungd.setLanguage(en);
	    samsungd.setName("Samsung");
	    samsungd.setManufacturer(samsung);
	    samsung.getDescriptions().add(samsungd);

	    manufacturerService.create(samsung);
	    
	    
	    Manufacturer lg = new Manufacturer();
	    lg.setMerchantStore(store);
	    lg.setCode("lg");

	    ManufacturerDescription lgd = new ManufacturerDescription();
	    lgd.setLanguage(en);
	    lgd.setName("LG");
	    lgd.setManufacturer(lg);
	    lg.getDescriptions().add(lgd);

	    manufacturerService.create(lg);
	    
	    Manufacturer sony = new Manufacturer();
	    sony.setMerchantStore(store);
	    sony.setCode("sony");

	    ManufacturerDescription sonyd = new ManufacturerDescription();
	    sonyd.setLanguage(en);
	    sonyd.setName("Sony");
	    sonyd.setManufacturer(sony);
	    sony.getDescriptions().add(sonyd);

	    manufacturerService.create(sony);

	    Manufacturer nokia = new Manufacturer();
	    nokia.setMerchantStore(store);
	    nokia.setCode("nokia");

	    ManufacturerDescription nokiad = new ManufacturerDescription();
	    nokiad.setLanguage(en);
	    nokiad.setManufacturer(nokia);
	    nokiad.setName("Nokia");
	    nokia.getDescriptions().add(nokiad);

	    manufacturerService.create(nokia);

	    Manufacturer apple = new Manufacturer();
	    apple.setMerchantStore(store);
	    apple.setCode("apple");

	    ManufacturerDescription appled = new ManufacturerDescription();
	    appled.setLanguage(en);
	    appled.setManufacturer(apple);
	    appled.setName("Novells publishing");
	    apple.getDescriptions().add(appled);

	    manufacturerService.create(apple);
		
	}

}
