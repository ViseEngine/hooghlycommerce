package co.hooghly.commerce.domain;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ProductCriteria extends Criteria {
	
	
	private String productName;
	private List<AttributeCriteria> attributeCriteria;

	
	private Boolean available = null;
	
	private List<Long> categoryIds;
	private List<String> availabilities;
	private List<Long> productIds;
	
	private Long manufacturerId = null;

	

}
