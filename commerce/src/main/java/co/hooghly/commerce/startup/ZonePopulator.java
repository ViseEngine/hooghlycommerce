package co.hooghly.commerce.startup;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.domain.ZoneDescription;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(4)
public class ZonePopulator extends AbstractDataPopulator {
	
	public ZonePopulator() {
		super("ZONE");
	}

	@Autowired
	private ZoneService zoneService;

	@Autowired
	private LanguageService languageService;

	@Autowired
	private CountryService countryService;

	@Override
	public void runInternal(String... args) throws Exception {
		log.info("4.Loading zones.");
		createZones();

	}

	private void createZones() throws ServiceException {
		
		try {

			Map<String, Zone> zonesMap = new HashMap<String, Zone>();
			zonesMap = loadZones("reference/zoneconfig.json");

			for (Map.Entry<String, Zone> entry : zonesMap.entrySet()) {
				String key = entry.getKey();
				Zone value = entry.getValue();
				if (value.getDescriptions() == null) {
					log.warn("This zone " + key + " has no descriptions");
					continue;
				}

				List<ZoneDescription> zoneDescriptions = value.getDescriptions();
				
				for (ZoneDescription description : value.getDescriptions()) {
					description.setZone(value);
					
				}
				
				zoneService.create(value);
			}

		} catch (Exception e) {

			throw new ServiceException(e);
		}

	}

	public Map<String, Zone> loadZones(String jsonFilePath) throws Exception {

		List<Language> languages = languageService.getLanguages();

		List<Country> countries = countryService.list();
		Map<String, Country> countriesMap = new HashMap<String, Country>();
		for (Country country : countries) {

			countriesMap.put(country.getIsoCode(), country);

		}

		ObjectMapper mapper = new ObjectMapper();

		try {

			InputStream in = this.getClass().getClassLoader().getResourceAsStream(jsonFilePath);

			@SuppressWarnings("unchecked")
			Map<String, Object> data = mapper.readValue(in, Map.class);

			Map<String, Zone> zonesMap = new HashMap<String, Zone>();
			Map<String, List<ZoneDescription>> zonesDescriptionsMap = new HashMap<String, List<ZoneDescription>>();
			Map<String, String> zonesMark = new HashMap<String, String>();

			for (Language l : languages) {
				@SuppressWarnings("rawtypes")
				List langList = (List) data.get(l.getCode());
				if (langList != null) {
					for (Object z : langList) {
						@SuppressWarnings("unchecked")
						Map<String, String> e = (Map<String, String>) z;
						String zoneCode = e.get("zoneCode");
						ZoneDescription zoneDescription = new ZoneDescription();
						zoneDescription.setLanguage(l);
						zoneDescription.setName(e.get("zoneName"));
						Zone zone = null;
						List<ZoneDescription> descriptions = null;
						if (!zonesMap.containsKey(zoneCode)) {
							zone = new Zone();
							Country country = countriesMap.get(e.get("countryCode"));
							if (country == null) {
								log.warn("Country is null for " + zoneCode + " and country code "
										+ e.get("countryCode"));
								continue;
							}
							zone.setCountry(country);
							zonesMap.put(zoneCode, zone);
							zone.setCode(zoneCode);

						}

						if (zonesMark.containsKey(l.getCode() + "_" + zoneCode)) {
							log.warn("This zone seems to be a duplicate !  " + zoneCode + " and language code "
									+ l.getCode());
							continue;
						}

						zonesMark.put(l.getCode() + "_" + zoneCode, l.getCode() + "_" + zoneCode);

						if (zonesDescriptionsMap.containsKey(zoneCode)) {
							descriptions = zonesDescriptionsMap.get(zoneCode);
						} else {
							descriptions = new ArrayList<ZoneDescription>();
							zonesDescriptionsMap.put(zoneCode, descriptions);
						}

						descriptions.add(zoneDescription);

					}
				}

			}

			for (Map.Entry<String, Zone> entry : zonesMap.entrySet()) {
				String key = entry.getKey();
				Zone value = entry.getValue();

				// if(value.getDescriptions()==null) {
				// LOGGER.warn("This zone " + key + " has no descriptions");
				// continue;
				// }

				// get descriptions
				List<ZoneDescription> descriptions = zonesDescriptionsMap.get(key);
				if (descriptions != null) {
					value.setDescriptons(descriptions);
				}
			}

			return zonesMap;

		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}
}
