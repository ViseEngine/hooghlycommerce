package co.hooghly.commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.SystemConfiguration;

public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {

	Optional<SystemConfiguration> findByKey(String key);

}
