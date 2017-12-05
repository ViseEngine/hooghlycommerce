package co.hooghly.commerce.business;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import co.hooghly.commerce.business.utils.CacheUtils;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.CountryDescription;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.repository.CountryRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CountryService extends SalesManagerEntityServiceImpl<Long, Country> {


	private CountryRepository countryRepository;

	@Autowired
	private CacheUtils cache;

	public CountryService(CountryRepository countryRepository) {
		super(countryRepository);
		this.countryRepository = countryRepository;
	}
	
	@Cacheable("countryCache")
	public Country getByCode(String code)  {
		return countryRepository.findByIsoCode(code);
	}

	public void addCountryDescription(Country country, CountryDescription description) {
		country.getDescriptions().add(description);
		description.setCountry(country);
		update(country);
	}

	public Map<String, Country> getCountriesMap(Language language)  {

		List<Country> countries = this.getCountries(language);

		Map<String, Country> returnMap = new LinkedHashMap<String, Country>();

		for (Country country : countries) {
			returnMap.put(country.getIsoCode(), country);
		}

		return returnMap;
	}

	public List<Country> getCountries(final List<String> isoCodes, final Language language)  {
		List<Country> countryList = getCountries(language);
		List<Country> requestedCountryList = new ArrayList<Country>();
		if (!CollectionUtils.isEmpty(countryList)) {
			for (Country c : countryList) {
				if (isoCodes.contains(c.getIsoCode())) {
					requestedCountryList.add(c);
				}
			}
		}
		return requestedCountryList;
	}

	@SuppressWarnings("unchecked")

	public List<Country> getCountries(Language language) {

		List<Country> countries = null;
		try {

			countries = (List<Country>) cache.getFromCache("COUNTRIES_" + language.getCode());

			if (countries == null) {

				countries = countryRepository.listByLanguage(language.getId());

				// set names
				for (Country country : countries) {

					CountryDescription description = country.getDescriptions().get(0);
					country.setName(description.getName());

				}

				cache.putInCache(countries, "COUNTRIES_" + language.getCode());
			}

		} catch (Exception e) {
			log.error("getCountries()", e);
		}

		return countries;

	}

}
