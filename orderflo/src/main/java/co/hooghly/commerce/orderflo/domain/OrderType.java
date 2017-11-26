package co.hooghly.commerce.orderflo.domain;

import java.util.Date;

import lombok.Data;

@Data
public class OrderType {

	private String code;
	private String processName;
	private boolean active;
	private Date created;
	private Date updatedDate;
	private String name;

}
