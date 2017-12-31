package co.hooghly.commerce.web.ui;

import java.io.Serializable;

import lombok.Data;

@Data
public class CategoryEntity extends Category implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	

	private int sortOrder;
	private boolean visible;
	private String lineage;
	private int depth;
	private Category parent;
	

	

}
