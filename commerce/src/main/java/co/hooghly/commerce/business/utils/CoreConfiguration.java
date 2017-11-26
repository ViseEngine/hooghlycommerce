package co.hooghly.commerce.business.utils;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:hooghly.properties")
@ConfigurationProperties("hooghly")
public class CoreConfiguration {

	public Properties properties = new Properties();

	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public String getProperty(String propertyKey) {

		return properties.getProperty(propertyKey);

	}

	public String getProperty(String propertyKey, String defaultValue) {
		return properties.getProperty(propertyKey, defaultValue);

	}

}
