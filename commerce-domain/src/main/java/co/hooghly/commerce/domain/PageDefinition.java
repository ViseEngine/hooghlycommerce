package co.hooghly.commerce.domain;


import java.util.Map;

import lombok.Data;

@Data
public class PageDefinition {
	
	
	private String name;
	
	private String layout = "layouts/default";
	
	private String title;
	
	private Map<String,String> fragments;
	
}