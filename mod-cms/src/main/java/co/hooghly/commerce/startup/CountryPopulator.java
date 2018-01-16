package co.hooghly.commerce.startup;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.constants.SchemaConstant;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.CountryDescription;
import co.hooghly.commerce.domain.Language;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(3)
public class CountryPopulator extends AbstractDataPopulator{
	
	public CountryPopulator() {
		super("COUNTRY");
	}
	
	@Autowired
	private LanguageService languageService;
	
	@Autowired
	private CountryService countryService;

	@Override
	public void runInternal(String... args) throws Exception {
		log.info("3.Loading countries.");
		
		createCountries();
		
	}
	
	private void createCountries()  {
		
		List<Language> languages = languageService.getLanguages();
		for(String code : SchemaConstant.COUNTRY_ISO_CODE) {
			Locale locale = SchemaConstant.LOCALES.get(code);
			if (locale != null) {
				Country country = new Country(code);
				
				
				for (Language language : languages) {
					String name = locale.getDisplayCountry(new Locale(language.getCode()));
					CountryDescription description = new CountryDescription(language, name);
					
					description.setCountry(country);
					country.getDescriptions().add(description);
				}
				
				countryService.create(country);
			}
		}
	}
}
