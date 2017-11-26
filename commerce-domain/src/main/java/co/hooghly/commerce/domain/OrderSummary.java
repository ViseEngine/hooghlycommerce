package co.hooghly.commerce.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * This object is used as input object for many services
 * such as order total calculation and tax calculation
 * 
 *
 */
public class OrderSummary implements Serializable {
	
	
	/**
	 * 
	 */
	private OrderSummaryType orderSummaryType = OrderSummaryType.ORDERTOTAL;
	private static final long serialVersionUID = 1L;
	private ShippingSummary shippingSummary;
	private List<ShoppingCartItem> products = new ArrayList<ShoppingCartItem>();

	public void setProducts(List<ShoppingCartItem> products) {
		this.products = products;
	}
	public List<ShoppingCartItem> getProducts() {
		return products;
	}
	public void setShippingSummary(ShippingSummary shippingSummary) {
		this.shippingSummary = shippingSummary;
	}
	public ShippingSummary getShippingSummary() {
		return shippingSummary;
	}
	public OrderSummaryType getOrderSummaryType() {
		return orderSummaryType;
	}
	public void setOrderSummaryType(OrderSummaryType orderSummaryType) {
		this.orderSummaryType = orderSummaryType;
	}

}
