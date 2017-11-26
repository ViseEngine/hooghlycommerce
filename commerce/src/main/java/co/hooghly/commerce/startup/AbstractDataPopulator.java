package co.hooghly.commerce.startup;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import co.hooghly.commerce.business.SystemConfigurationService;
import co.hooghly.commerce.business.SystemConstants;
import co.hooghly.commerce.domain.SystemConfiguration;

public abstract class AbstractDataPopulator implements CommandLineRunner {
	private String key;
	public AbstractDataPopulator(String key) {
		this.key = key;
	}
	
	@Autowired
	private SystemConfigurationService systemConfigurationService;
	
	protected boolean isDataLoadRequired(String key) {
		boolean loadingReq = true;
		Optional<SystemConfiguration> systemConfig = systemConfigurationService.getByKey(key);

		if(systemConfig.isPresent() && systemConfig.get().getValue().equals("LOADED")) {
			loadingReq = false;
			
		}
		
		if(loadingReq) {
			SystemConfiguration configuration = new SystemConfiguration();
			configuration.getAuditSection().setModifiedBy(SystemConstants.SYSTEM_USER);
			configuration.setKey(key);
			configuration.setValue("LOADED");
			systemConfigurationService.create(configuration);
		}
		
		return loadingReq;
	}
	

	@Override
	public void run(String... args) throws Exception {
		if(isDataLoadRequired(key))
			runInternal(args);
	}
	
	public abstract void runInternal(String... args) throws Exception;
	
	
}
