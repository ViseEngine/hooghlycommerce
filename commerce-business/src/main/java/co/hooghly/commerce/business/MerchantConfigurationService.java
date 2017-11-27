package co.hooghly.commerce.business;

import java.util.List;


import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.hooghly.commerce.domain.MerchantConfig;
import co.hooghly.commerce.domain.MerchantConfiguration;
import co.hooghly.commerce.domain.MerchantConfigurationType;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.repository.MerchantConfigurationRepository;

@Service("merchantConfigurationService")
public class MerchantConfigurationService extends SalesManagerEntityServiceImpl<Long, MerchantConfiguration> {

	private MerchantConfigurationRepository merchantConfigurationRepository;

	public MerchantConfigurationService(MerchantConfigurationRepository merchantConfigurationRepository) {
		super(merchantConfigurationRepository);
		this.merchantConfigurationRepository = merchantConfigurationRepository;
	}

	public MerchantConfiguration getMerchantConfiguration(String key, MerchantStore store) throws ServiceException {
		return merchantConfigurationRepository.findByMerchantStoreAndKey(store.getId(), key);
	}

	public List<MerchantConfiguration> listByStore(MerchantStore store) throws ServiceException {
		return merchantConfigurationRepository.findByMerchantStore(store.getId());
	}

	public List<MerchantConfiguration> listByType(MerchantConfigurationType type, MerchantStore store)
			throws ServiceException {
		return merchantConfigurationRepository.findByMerchantStoreAndType(store.getId(), type);
	}

	public void saveOrUpdate(MerchantConfiguration entity) throws ServiceException {

		if (entity.getId() != null && entity.getId() > 0) {
			super.update(entity);
		} else {
			super.create(entity);

		}
	}

	public void delete(MerchantConfiguration merchantConfiguration) {
		MerchantConfiguration config = merchantConfigurationRepository.findOne(merchantConfiguration.getId());
		if (config != null) {
			super.delete(config);
		}
	}

	public MerchantConfig getMerchantConfig(MerchantStore store) {

		MerchantConfiguration configuration = merchantConfigurationRepository.findByMerchantStoreAndKey(store.getId(),
				MerchantConfigurationType.CONFIG.name());

		MerchantConfig config = null;
		if (configuration != null) {
			String value = configuration.getValue();

			ObjectMapper mapper = new ObjectMapper();
			try {
				config = mapper.readValue(value, MerchantConfig.class);
			} catch (Exception e) {
				throw new ServiceException("Cannot parse json string " + value);
			}
		}
		return config;

	}

	public void saveMerchantConfig(MerchantConfig config, MerchantStore store) throws ServiceException {

		MerchantConfiguration configuration = merchantConfigurationRepository.findByMerchantStoreAndKey(store.getId(),
				MerchantConfigurationType.CONFIG.name());

		if (configuration == null) {
			configuration = new MerchantConfiguration();
			configuration.setMerchantStore(store);
			configuration.setKey(MerchantConfigurationType.CONFIG.name());
			configuration.setMerchantConfigurationType(MerchantConfigurationType.CONFIG);
		}

		String value = config.toJSONString();
		configuration.setValue(value);
		if (configuration.getId() != null && configuration.getId() > 0) {
			super.update(configuration);
		} else {
			super.create(configuration);

		}

	}

}
