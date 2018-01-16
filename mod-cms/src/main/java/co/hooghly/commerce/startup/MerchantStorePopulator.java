package co.hooghly.commerce.startup;

import java.util.Arrays;
import java.util.Date;
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
import co.hooghly.commerce.domain.Address;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Currency;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.TaxClass;
import co.hooghly.commerce.domain.Zone;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(6)
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

	@Override
	public void runInternal(String... args) throws Exception {
		createMerchant();

	}

	private void createMerchant() {
		log.info("6.Populating merchant store");

		Date date = new Date(System.currentTimeMillis());

		Language en = languageService.getByCode("en");
		Language hi = languageService.getByCode("hi");

		Country in = countryService.getByCode("IN");
		Currency currency = currencyService.getByCode("INR");

		Zone qc = zoneService.getByCode("QC");

		List<Language> supportedLanguages = Arrays.asList(en, hi);

		// create a merchant
		MerchantStore store = new MerchantStore();
		store.setCountry(in);
		store.setCurrency(currency);
		store.setDefaultLanguage(en);
		store.setInBusinessSince(date);
		store.setZone(qc);
		store.setStorename("Default store");
		store.setStorephone("888-888-8888");
		store.setCode(MerchantStore.DEFAULT_STORE);
		store.setTheme("zap");
		Address address = new Address();
		address.setCity("My city");
		address.setStreet("1234 Street address");
		address.setPostalCode("H2H-2H2");
		store.setAddress(address);
		store.setStoreEmailAddress("test@test.com");
		store.setDomainName("localhost:8080");
		store.setStoreTemplate("bootstrap");
		store.setLanguages(supportedLanguages);

		merchantService.save(store);

		log.info("6.1 Populating Tax Class - DEFAULT");

		TaxClass taxclass = new TaxClass(TaxClass.DEFAULT_TAX_CLASS);
		taxclass.setMerchantStore(store);

		taxClassService.saveNow(taxclass);

		

	}
}
