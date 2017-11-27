package co.hooghly.commerce.domain;

import java.util.List;

import lombok.Data;

/**
 * Holder for shipping meta data
 *
 */
@Data
public class ShippingMetaData {
	
	private List<String> modules;
	private List<String> preProcessors;
	private List<String> postProcessors;
	private List<Country> shipToCountry;
	private boolean useDistanceModule;
	
}
