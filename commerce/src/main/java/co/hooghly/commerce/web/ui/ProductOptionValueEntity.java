package co.hooghly.commerce.web.ui;

import java.io.Serializable;

public class ProductOptionValueEntity extends ProductOptionValue implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int order;
	
	public void setOrder(int order) {
		this.order = order;
	}
	public int getOrder() {
		return order;
	}


}
