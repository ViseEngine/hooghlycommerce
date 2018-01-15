package co.hooghly.commerce.domain;

import lombok.Data;

@Data
public class Address  {
	
	private String street;
	private String city;
	private String postalCode;
	private String stateProvince;
	private String zone;
	private String country;

	
}
