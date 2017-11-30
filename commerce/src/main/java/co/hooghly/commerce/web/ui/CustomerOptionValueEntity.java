package co.hooghly.commerce.web.ui;

import java.io.Serializable;

import lombok.Data;

@Data
public class CustomerOptionValueEntity extends CustomerOptionValue implements
		Serializable {

	
	private int order;
	private String code;
	

}
