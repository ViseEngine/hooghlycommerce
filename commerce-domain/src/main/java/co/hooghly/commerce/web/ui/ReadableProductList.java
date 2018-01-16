package co.hooghly.commerce.web.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReadableProductList implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int totalCount;
	
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}
	public int getTotalCount() {
		return totalCount;
	}

}
