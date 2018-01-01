package co.hooghly.commerce.startup;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.ManufacturerService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.MessageResourceService;
import co.hooghly.commerce.domain.Manufacturer;
import co.hooghly.commerce.domain.MerchantStore;

import co.hooghly.commerce.domain.MessageResource;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(11)
public class ManufacturerPopulator extends AbstractDataPopulator {

	@Value("classpath:demo-data/manufacturer_*.txt")
	private Resource[] resources;

	public ManufacturerPopulator() {
		super("MANUFACTURER");
	}

	@Autowired
	protected ManufacturerService manufacturerService;

	@Autowired
	protected MerchantStoreService merchantService;
	
	@Autowired
	private MessageResourceService messageResourceService;

	@Override
	public void runInternal(String... args) throws Exception {

		log.info("11.Populating manufacturer.");

		MerchantStore store = merchantService.getMerchantStore(MerchantStore.DEFAULT_STORE);
		

		for (Resource r : resources) {
			List<String> contents = getFileContent(r.getURI());
			List<MessageResource> messageResources = new ArrayList<>();
			List<Manufacturer> manufacturers = new ArrayList<>();
			for (String l : contents) {
				String items[] = StringUtils.split(l,",");

				MessageResource mr = new MessageResource();
				mr.setDomain("Manufacturer");
				//mr.setLocale(storeViewDefaultEn.computeLocale().toString());
				mr.setMessageKey(items[0]);
				mr.setMessageText(items[2]);

				messageResources.add(mr);

				mr = new MessageResource();
				mr.setDomain("Manufacturer");
				//mr.setLocale(storeView.computeLocale().toString());
				mr.setMessageKey(items[0]);
				mr.setMessageText(items[3]);
				
				messageResources.add(mr);
				
				Manufacturer manufacturer = new Manufacturer();
				manufacturer.setMerchantStore(store);
				manufacturer.setCode(items[1]);
				manufacturer.setName(items[2]);
				manufacturers.add(manufacturer);
			}
			
			manufacturerService.save(manufacturers);
			messageResourceService.save(messageResources);
		}

		

	}

}
