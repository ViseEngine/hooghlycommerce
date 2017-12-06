package co.hooghly.commerce.startup;


import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import co.hooghly.commerce.domain.PageDefinitions;
import co.hooghly.commerce.domain.SystemConfiguration;
import co.hooghly.commerce.repository.PageDefinitionRepository;
import co.hooghly.commerce.repository.SystemConfigurationRepository;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(20)
public class PageDefinitionPopulator implements CommandLineRunner {
	
	@Value("classpath*:pagedefs/*-pagedef.yml")
	private Resource[] resources;
	
	@Autowired
	private SystemConfigurationRepository systemConfigurationRepository;
	
	@Autowired
	private PageDefinitionRepository pageDefinitionRepository;

	protected boolean isDataLoadRequired(String key) {
		boolean loadingReq = true;
		Optional<SystemConfiguration> systemConfig = systemConfigurationRepository.findByKey(key);

		if(systemConfig.isPresent() && systemConfig.get().getValue().equals("LOADED")) {
			loadingReq = false;
			
		}
		
		if(loadingReq) {
			SystemConfiguration configuration = new SystemConfiguration();
			configuration.getAuditSection().setModifiedBy("SYSTEM");
			configuration.setKey(key);
			configuration.setValue("LOADED");
			systemConfigurationRepository.save(configuration);
		}
		
		return loadingReq;
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("12.Populating products");
		if(isDataLoadRequired("PAGE-DEFINITION")) {
			log.info("### Loading page definition information ###");

			for (Resource r : resources) {
				try {
					log.info("#Resource -  {}", r.getFilename());

					Yaml yaml = new Yaml();
					PageDefinitions pgDefs = yaml.loadAs(r.getInputStream(), PageDefinitions.class);

					log.debug("#pgDefs -  {}", pgDefs);

					//save page definitions
					
					pageDefinitionRepository.save(pgDefs.getDefs());

				} catch (Exception e) {
					log.error("Error", e);
				}
			}

			
		}
	}

	

}
