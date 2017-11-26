package co.hooghly.commerce.startup;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.CmsContentService;
import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.CurrencyService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.TaxClassService;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Currency;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.MerchantStoreView;
import co.hooghly.commerce.domain.TaxClass;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.repository.MerchantStoreViewRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(7)
public class MerchantStorePopulator extends AbstractDataPopulator {
	
	public MerchantStorePopulator() {
		super("MERCHANT");
	}

	@Autowired
	private ZoneService zoneService;

	@Autowired
	private LanguageService languageService;

	@Autowired
	private CountryService countryService;

	@Autowired
	private CurrencyService currencyService;

	@Autowired
	protected MerchantStoreService merchantService;

	@Autowired
	private TaxClassService taxClassService;
	
	@Autowired
	private CmsContentService cmsContentService;
	@Autowired
	private MerchantStoreViewRepository merchantStoreViewRepository;

	@Override
	public void runInternal(String... args) throws Exception {
		createMerchant();

	}

	private void createMerchant()  {
		log.info("7.Populating merchant ");

		Date date = new Date(System.currentTimeMillis());

		Language en = languageService.getByCode("en");
		Language fr = languageService.getByCode("fr");
		Country ca = countryService.getByCode("CA");
		Currency currency = currencyService.getByCode("GBP");
		Currency eur = currencyService.getByCode("EUR");
		
		log.info("EURO - {}", eur);
		
		Zone qc = zoneService.getByCode("QC");

		List<Language> supportedLanguages = new ArrayList<Language>();
		supportedLanguages.add(en);

		// create a merchant
		MerchantStore store = new MerchantStore();
		store.setCountry(ca);
		store.setCurrency(currency);
		store.setDefaultLanguage(en);
		store.setInBusinessSince(date);
		store.setZone(qc);
		store.setStorename("Default store");
		store.setStorephone("888-888-8888");
		store.setCode(MerchantStore.DEFAULT_STORE);
		store.setStorecity("My city");
		store.setStoreaddress("1234 Street address");
		store.setStorepostalcode("H2H-2H2");
		store.setStoreEmailAddress("test@test.com");
		store.setDomainName("localhost:8080");
		store.setStoreTemplate("bootstrap");
		store.setLanguages(supportedLanguages);

		merchantService.create(store);
		
		log.info("7.1 Populating store views along with store");
		MerchantStoreView eng = new MerchantStoreView();
		log.info("Curr 1 - {}", currency.getId());
		eng.setCurrency(currency);
		eng.setLanguage(en);
		eng.setTheme("zap");
		eng.setMerchantStore(store);
		eng.setDefaultView(true);
		
		merchantStoreViewRepository.save(eng);
		
		MerchantStoreView frStoreView = new MerchantStoreView();
		log.info("Curr 2 - {}", eur.getId());
		frStoreView.setCurrency(eur);
		frStoreView.setLanguage(fr);
		frStoreView.setTheme("zap");
		frStoreView.setMerchantStore(store);
		
		
		merchantStoreViewRepository.save(frStoreView);
		
		
		
		
		log.info("7.2 Populating Tax Class - DEFAULT");
		
		TaxClass taxclass = new TaxClass(TaxClass.DEFAULT_TAX_CLASS);
		taxclass.setMerchantStore(store);

		taxClassService.saveNow(taxclass);
		
		log.info("7.3 Populating CMS contents - DEMO");
		
		cmsContentService.load(store);
		
	}
}