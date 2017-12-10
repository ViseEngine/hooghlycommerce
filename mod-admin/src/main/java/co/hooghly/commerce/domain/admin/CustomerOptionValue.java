package co.hooghly.commerce.domain.admin;

import java.io.Serializable;

import co.hooghly.commerce.web.ui.ShopEntity;



public class CustomerOptionValue extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
