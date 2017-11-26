package co.hooghly.commerce.orderflo.domain;

import java.util.Date;
import java.util.Map;

import lombok.Data;

@Data
public class Order {
	
	 private OrderType orderType;
	 private User user;
	 private Long id;
	 private String externalId;
	 private String processId;
	 private String processState;
	 private String orderStatus;
	 private Date completionEnd;
	 private boolean archived;
	 private Map<String,?> data;
	 private Date created;
	 private Date updated;
}
