package co.hooghly.commerce.config;


import org.springframework.beans.factory.annotation.Value;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableCaching()
@EnableJpaRepositories(basePackages = {  "co.hooghly.commerce.repository" })

public class CoreApplicationConfiguration {

	@Value("${secretKey}")
	private String secret;

	@Bean
	public String secretKey() {
		return secret;
	}


}
