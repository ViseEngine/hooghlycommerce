package co.hooghly.commerce.domain;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class PageDefinitions  {
	
	private List<PageDefinition> defs = new ArrayList<>();
}