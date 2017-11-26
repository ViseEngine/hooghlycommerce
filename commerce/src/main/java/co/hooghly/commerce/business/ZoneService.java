package co.hooghly.commerce.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.business.utils.CacheUtils;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.domain.ZoneDescription;
import co.hooghly.commerce.repository.ZoneRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ZoneService extends SalesManagerEntityServiceImpl<Long, Zone> {

	private final static String ZONE_CACHE_PREFIX = "ZONES_";

	private ZoneRepository zoneRepository;

	@Inject
	private CacheUtils cache;

	

	public ZoneService(ZoneRepository zoneRepository) {
		super(zoneRepository);
		this.zoneRepository = zoneRepository;
	}

	public Zone getByCode(String code) {
		return zoneRepository.findByCode(code);
	}

	public void addDescription(Zone zone, ZoneDescription description) throws ServiceException {
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

	@SuppressWarnings("unchecked")

	public List<Zone> getZones(Country country, Language language) {

		List<Zone> zones = null;
		try {

			String cacheKey = ZONE_CACHE_PREFIX + country.getIsoCode() + Constants.UNDERSCORE + language.getCode();

			zones = (List<Zone>) cache.getFromCache(cacheKey);

			if (zones == null) {

				zones = zoneRepository.listByLanguageAndCountry(country.getIsoCode(), language.getId());

				// set names
				for (Zone zone : zones) {
					ZoneDescription description = zone.getDescriptions().get(0);
					zone.setName(description.getName());

				}
				cache.putInCache(zones, cacheKey);
			}

		} catch (Exception e) {
			log.error("getZones()", e);
		}
		return zones;

	}

	@SuppressWarnings("unchecked")
	public Map<String, Zone> getZones(Language language) {

		Map<String, Zone> zones = null;
		try {

			String cacheKey = ZONE_CACHE_PREFIX + language.getCode();

			zones = (Map<String, Zone>) cache.getFromCache(cacheKey);

			if (zones == null) {
				zones = new HashMap<String, Zone>();
				List<Zone> zns = zoneRepository.listByLanguage(language.getId());

				// set names
				for (Zone zone : zns) {
					ZoneDescription description = zone.getDescriptions().get(0);
					zone.setName(description.getName());
					zones.put(zone.getCode(), zone);

				}
				cache.putInCache(zones, cacheKey);
			}

		} catch (Exception e) {
			log.error("getZones()", e);
		}
		return zones;

	}

}
