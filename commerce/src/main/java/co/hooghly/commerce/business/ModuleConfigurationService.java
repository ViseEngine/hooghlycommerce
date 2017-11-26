package co.hooghly.commerce.business;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.json.simple.JSONArray;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.hooghly.commerce.business.utils.CacheUtils;
import co.hooghly.commerce.domain.IntegrationModule;
import co.hooghly.commerce.domain.ModuleConfig;
import co.hooghly.commerce.repository.ModuleConfigurationRepository;

@Service
public class ModuleConfigurationService extends SalesManagerEntityServiceImpl<Long, IntegrationModule> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModuleConfigurationService.class);

	private ModuleConfigurationRepository moduleConfigurationRepository;

	@Inject
	private CacheUtils cache;

	@Inject
	public ModuleConfigurationService(ModuleConfigurationRepository moduleConfigurationRepository) {
		super(moduleConfigurationRepository);
		this.moduleConfigurationRepository = moduleConfigurationRepository;
	}

	public IntegrationModule getByCode(String moduleCode) {
		return moduleConfigurationRepository.findByCode(moduleCode);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })

	public List<IntegrationModule> getIntegrationModules(String module) {

		List<IntegrationModule> modules = null;
		try {

			// CacheUtils cacheUtils = CacheUtils.getInstance();
			modules = (List<IntegrationModule>) cache.getFromCache("INTEGRATION_M)" + module);
			if (modules == null) {
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
				cache.putInCache(modules, "INTEGRATION_M)" + module);
			}

		} catch (Exception e) {
			LOGGER.error("getIntegrationModules()", e);
		}
		return modules;

	}

	public void createOrUpdateModule(String json) throws ServiceException {

		ObjectMapper mapper = new ObjectMapper();

		try {

			@SuppressWarnings("rawtypes")
			Map object = mapper.readValue(json, Map.class);

			//IntegrationModule module = integrationModulesLoader.loadModule(object);
			
			IntegrationModule module = null;

			if (module != null) {
				IntegrationModule m = this.getByCode(module.getCode());
				if (m != null) {
					this.delete(m);
				}
				this.create(module);
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

}
