package co.hooghly.commerce.domain;

import java.io.Serializable;

import lombok.Data;

@Data
public class Address implements Serializable {
	

	private static final long serialVersionUID = 1L;
	private String city;
	private String postalCode;
	private String stateProvince;
	private String zone;//code
	private String country;//code

	
}
