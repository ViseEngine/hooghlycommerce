package co.hooghly.commerce.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.hooghly.commerce.domain.IntegrationModule;
import co.hooghly.commerce.domain.ModuleConfig;
import co.hooghly.commerce.repository.ModuleConfigurationRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModuleConfigurationService extends SalesManagerEntityServiceImpl<Long, IntegrationModule> {

	private ModuleConfigurationRepository moduleConfigurationRepository;

	public ModuleConfigurationService(ModuleConfigurationRepository moduleConfigurationRepository) {
		super(moduleConfigurationRepository);
		this.moduleConfigurationRepository = moduleConfigurationRepository;
	}

	@Cacheable("integrationModuleByCode")
	public IntegrationModule getByCode(String moduleCode) {
		return moduleConfigurationRepository.findByCode(moduleCode);
	}

	@Cacheable("integrationModules")
	public List<IntegrationModule> getIntegrationModules(String module) {

		List<IntegrationModule> modules = null;

		modules = moduleConfigurationRepository.findByModule(module);
		// set json objects
		for (IntegrationModule mod : modules) {

			String regions = mod.getRegions();
			if (regions != null) {
				Object objRegions = JSONValue.parse(regions);
				JSONArray arrayRegions = (JSONArray) objRegions;
				Iterator i = arrayRegions.iterator();
				while (i.hasNext()) {
					mod.getRegionsSet().add((String) i.next());
				}
			}

			String details = mod.getConfigDetails();
			if (details != null) {

				// Map objects = mapper.readValue(config, Map.class);

				Map<String, String> objDetails = (Map<String, String>) JSONValue.parse(details);
				mod.setDetails(objDetails);

			}

			String configs = mod.getConfiguration();
			if (configs != null) {

				// Map objects = mapper.readValue(config, Map.class);

				Object objConfigs = JSONValue.parse(configs);
				JSONArray arrayConfigs = (JSONArray) objConfigs;

				Map<String, ModuleConfig> moduleConfigs = new HashMap<String, ModuleConfig>();

				Iterator i = arrayConfigs.iterator();
				while (i.hasNext()) {

					Map values = (Map) i.next();
					String env = (String) values.get("env");
					ModuleConfig config = new ModuleConfig();
					config.setScheme((String) values.get("scheme"));
					config.setHost((String) values.get("host"));
					config.setPort((String) values.get("port"));
					config.setUri((String) values.get("uri"));
					config.setEnv((String) values.get("env"));
					if ((String) values.get("config1") != null) {
						config.setConfig1((String) values.get("config1"));
					}
					if ((String) values.get("config2") != null) {
						config.setConfig1((String) values.get("config2"));
					}

					moduleConfigs.put(env, config);

				}

				mod.setModuleConfigs(moduleConfigs);

			}

		}

		return modules;

	}

	public IntegrationModule createOrUpdateModule(String json) {

		ObjectMapper mapper = new ObjectMapper();

		try {
			Map object = mapper.readValue(json, Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// IntegrationModule module =
		// integrationModulesLoader.loadModule(object);

		IntegrationModule module = null;

		if (module != null) {
			IntegrationModule m = this.getByCode(module.getCode());
			if (m != null) {
				this.delete(m);
			}
			this.create(module);
		}
		
		return module;
	}

}
