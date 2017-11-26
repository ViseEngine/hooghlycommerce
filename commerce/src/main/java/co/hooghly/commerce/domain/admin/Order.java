package co.hooghly.commerce.domain.admin;

import co.hooghly.commerce.domain.Billing;
import co.hooghly.commerce.domain.Delivery;
import co.hooghly.commerce.domain.OrderStatus;

import javax.persistence.Embedded;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


public class Order implements Serializable {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long Id;
	private String orderHistoryComment = "";
	
	List<OrderStatus> orderStatusList = Arrays.asList(OrderStatus.values());     
	private String datePurchased = "";
	private  co.hooghly.commerce.domain.Order order;
	
	@Embedded
	private co.hooghly.commerce.domain.Delivery delivery = null;
	
	@Embedded
	private co.hooghly.commerce.domain.Billing billing = null;
	
	
	
	
	public String getDatePurchased() {
		return datePurchased;
	}

	public void setDatePurchased(String datePurchased) {
		this.datePurchased = datePurchased;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getOrderHistoryComment() {
		return orderHistoryComment;
	}

	public void setOrderHistoryComment(String orderHistoryComment) {
		this.orderHistoryComment = orderHistoryComment;
	}

	public List<OrderStatus> getOrderStatusList() {
		return orderStatusList;
	}

	public void setOrderStatusList(List<OrderStatus> orderStatusList) {
		this.orderStatusList = orderStatusList;
	}

	public co.hooghly.commerce.domain.Order getOrder() {
		return order;
	}

	public void setOrder(co.hooghly.commerce.domain.Order order) {
		this.order = order;
	}

	public Delivery getDelivery() {
		return delivery;
	}

	public void setDelivery(Delivery delivery) {
		this.delivery = delivery;
	}

	public Billing getBilling() {
		return billing;
	}

	public void setBilling(Billing billing) {
		this.billing = billing;
	}




	
}