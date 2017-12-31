package co.hooghly.commerce.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.domain.ZoneDescription;
import co.hooghly.commerce.repository.ZoneRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ZoneService extends SalesManagerEntityServiceImpl<Long, Zone> {

	private ZoneRepository zoneRepository;

	public ZoneService(ZoneRepository zoneRepository) {
		super(zoneRepository);
		this.zoneRepository = zoneRepository;
	}

	@Cacheable("zoneByCode")
	public Zone getByCode(String code) {
		return zoneRepository.findByCode(code);
	}

	public void addDescription(Zone zone, ZoneDescription description) {
		if (zone.getDescriptions() != null) {
			if (!zone.getDescriptions().contains(description)) {
				zone.getDescriptions().add(description);
				update(zone);
			}
		} else {
			List<ZoneDescription> descriptions = new ArrayList<ZoneDescription>();
			descriptions.add(description);
			zone.setDescriptons(descriptions);
			update(zone);
		}
	}

	@Cacheable("zonesByCountryAndLanguage")
	public List<Zone> getZones(Country country, Language language) {

		List<Zone> zones = zoneRepository.listByLanguageAndCountry(country.getIsoCode(), language.getId());

		// set names
		for (Zone zone : zones) {
			ZoneDescription description = zone.getDescriptions().get(0);
			zone.setName(description.getName());

		}

		return zones;
	}

	@Cacheable("zonesByLanguage")
	public Map<String, Zone> getZones(Language language) {

		Map<String, Zone> zones = null;

		zones = new HashMap<String, Zone>();
		List<Zone> zns = zoneRepository.listByLanguage(language.getId());

		// set names
		for (Zone zone : zns) {
			ZoneDescription description = zone.getDescriptions().get(0);
			zone.setName(description.getName());
			zones.put(zone.getCode(), zone);

		}

		return zones;

	}

}
