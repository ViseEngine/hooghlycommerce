package co.hooghly.commerce.business;

import java.util.Optional;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.SystemConfiguration;
import co.hooghly.commerce.repository.SystemConfigurationRepository;

@Service
public class SystemConfigurationService extends SalesManagerEntityServiceImpl<Long, SystemConfiguration> {

	private SystemConfigurationRepository systemConfigurationReposotory;

	public SystemConfigurationService(SystemConfigurationRepository systemConfigurationReposotory) {
		super(systemConfigurationReposotory);
		this.systemConfigurationReposotory = systemConfigurationReposotory;
	}

	public Optional<SystemConfiguration> getByKey(String key) {
		return systemConfigurationReposotory.findByKey(key);
	}

}
