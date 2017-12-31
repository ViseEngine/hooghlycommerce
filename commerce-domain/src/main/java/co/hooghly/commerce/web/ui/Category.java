package co.hooghly.commerce.web.ui;

import java.io.Serializable;

import lombok.Data;

@Data
@Deprecated
public class Category extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	
	
}
