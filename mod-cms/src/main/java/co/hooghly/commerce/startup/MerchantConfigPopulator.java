package co.hooghly.commerce.startup;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.MerchantConfigurationService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.domain.MerchantConfig;
import co.hooghly.commerce.domain.MerchantStore;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(8)
public class MerchantConfigPopulator extends AbstractDataPopulator{
	
	public MerchantConfigPopulator() {
		super("MERCHANTCONFIG");
	}
	
	@Autowired
	private MerchantConfigurationService merchantConfigurationService;
	
	@Autowired
	protected MerchantStoreService merchantService;

	@Override
	public void runInternal(String... args) throws Exception {
		log.info("8.Populating merchant config populator.");
		
		MerchantStore store = merchantService.getByCode(MerchantStore.DEFAULT_STORE);
		MerchantConfig config = new MerchantConfig();
		config.setAllowPurchaseItems(true);
		config.setDisplayAddToCartOnFeaturedItems(true);

		merchantConfigurationService.saveMerchantConfig(config, store);

	}

}
