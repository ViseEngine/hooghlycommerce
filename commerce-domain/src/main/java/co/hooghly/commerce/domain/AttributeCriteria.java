package co.hooghly.commerce.domain;

import java.io.Serializable;

import lombok.Data;
@Data
public class AttributeCriteria implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String attributeCode;
	private String attributeValue;
	

}
