package co.hooghly.commerce.web.ui;

import java.io.Serializable;

import lombok.Data;

@Data
public class BreadcrumbItem implements Serializable {
	
	
	private static final long serialVersionUID = 1L;
	private Long id;
	private String label;
	private String url;
	private BreadcrumbItemType itemType;
	

}
