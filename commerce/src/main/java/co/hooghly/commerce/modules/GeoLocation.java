package co.hooghly.commerce.modules;

import java.net.InetAddress;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;

import co.hooghly.commerce.domain.Address;
import lombok.extern.slf4j.Slf4j;

/**
 * Using Geolite2 City database
 * http://dev.maxmind.com/geoip/geoip2/geolite2/#Databases
 *
 */
@Slf4j
@Component
public class GeoLocation {

	private DatabaseReader reader = null;

	@Value("classpath:reference/GeoLite2-City.mmdb")
	private Resource geoLocationDbFile;

	public Optional<Address> getAddress(String ipAddress) {

		Optional<Address> address = Optional.empty();
		try {
			Address addr = new Address();

			InetAddress ip = InetAddress.getByName(ipAddress);

			if (ip.isLoopbackAddress() || ip.isAnyLocalAddress() || ip.isLinkLocalAddress()
					|| ip.isSiteLocalAddress()) {
				addr.setCity("DALLAS");
				addr.setPostalCode("75024");
				addr.setCountry("USA");
				addr.setZone("DFW");
				address = Optional.of(addr);

			} else {

				CityResponse response = reader.city(InetAddress.getByName(ipAddress));

				addr.setCountry(response.getCountry().getIsoCode());
				addr.setPostalCode(response.getPostal().getCode());
				addr.setZone(response.getMostSpecificSubdivision().getIsoCode());
				addr.setCity(response.getCity().getName());
			}

			address = Optional.of(addr);
		} catch (Exception e) {
			log.warn("Address not found. Setting to empty");
		}
		return address;

	}

	@PostConstruct
	public void init() {
		try {
			reader = new DatabaseReader.Builder(geoLocationDbFile.getInputStream()).build();
		} catch (Exception e) {
			log.warn("Unable to start GEO-LOCATION DB - {}", e.getLocalizedMessage());
		}
	}

}
