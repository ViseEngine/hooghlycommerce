package co.hooghly.commerce.startup;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import co.hooghly.commerce.business.SystemConfigurationService;
import co.hooghly.commerce.business.SystemConstants;
import co.hooghly.commerce.domain.SystemConfiguration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		log.info("Loading key - {}", key);
		if(isDataLoadRequired(key))
			runInternal(args);
	}
	
	public abstract void runInternal(String... args) throws Exception;
	

	
	protected List<String> getFileContent(InputStream inputStream) throws IOException{
		return IOUtils.readLines(inputStream, StandardCharsets.UTF_8).stream().filter(i -> StringUtils.isNotBlank(i) && !StringUtils.startsWith(i, "#")).collect(Collectors.toList());
	}
}
