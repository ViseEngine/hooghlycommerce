package co.hooghly.commerce.domain;

import lombok.Data;

@Data
public class OrderCriteria extends Criteria {
	
	private String customerName;
	private String paymentMethod;
	private Long customerId;
	
	
	

}
