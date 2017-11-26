package co.hooghly.commerce.business;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import co.hooghly.commerce.business.utils.CacheUtils;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.CountryDescription;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.repository.CountryRepository;

@Service
public class CountryService extends SalesManagerEntityServiceImpl<Integer, Country>
		 {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CountryService.class);
	
	private CountryRepository countryRepository;
	
	@Inject
	private CacheUtils cache;

	
	
	public CountryService(CountryRepository countryRepository) {
		super(countryRepository);
		this.countryRepository = countryRepository;
	}
	
	public Country getByCode(String code) throws ServiceException {
		return countryRepository.findByIsoCode(code);
	}

	
	public void addCountryDescription(Country country, CountryDescription description) throws ServiceException {
		country.getDescriptions().add(description);
		description.setCountry(country);
		update(country);
	}
	
	
	public Map<String,Country> getCountriesMap(Language language) throws ServiceException {
		
		List<Country> countries = this.getCountries(language);
		
		Map<String,Country> returnMap = new LinkedHashMap<String,Country>();
		
		for(Country country : countries) {
			returnMap.put(country.getIsoCode(), country);
		}
		
		return returnMap;
	}
	
	
	
	public List<Country> getCountries(final List<String> isoCodes, final Language language) throws ServiceException {
		List<Country> countryList = getCountries(language);
		List<Country> requestedCountryList = new ArrayList<Country>();
		if(!CollectionUtils.isEmpty(countryList)) {
			for(Country c : countryList) {
				if(isoCodes.contains(c.getIsoCode())) {
					requestedCountryList.add(c);
				}
			}
		}
		return requestedCountryList;
	}
	
	
	@SuppressWarnings("unchecked")
	
	public List<Country> getCountries(Language language) throws ServiceException {
		
		List<Country> countries = null;
		try {

			countries = (List<Country>) cache.getFromCache("COUNTRIES_" + language.getCode());

		
		
			if(countries==null) {
			
				countries = countryRepository.listByLanguage(language.getId());
			
				//set names
				for(Country country : countries) {
					
					CountryDescription description = country.getDescriptions().get(0);
					country.setName(description.getName());
					
				}
				
				cache.putInCache(countries, "COUNTRIES_" + language.getCode());
			}
			
			
		
		
		
		} catch (Exception e) {
			LOGGER.error("getCountries()", e);
		}
		
		return countries;
		
		
	}


}
