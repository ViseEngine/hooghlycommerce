package co.hooghly.commerce.web.ui;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import groovy.transform.EqualsAndHashCode;
import lombok.Data;


@Component
@Scope(value = "prototype")
@Data
@EqualsAndHashCode(callSuper=true)
public class ShoppingCartData extends ShopEntity implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	private String code;
	private int quantity;
	private String total;
	private String subTotal;
	
	private List<OrderTotal> totals;//calculated from OrderTotalSummary
	private List<ShoppingCartItem> shoppingCartItems;
	private List<ShoppingCartItem> unavailables;
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public List<ShoppingCartItem> getShoppingCartItems() {
		return shoppingCartItems;
	}
	public void setShoppingCartItems(List<ShoppingCartItem> shoppingCartItems) {
		this.shoppingCartItems = shoppingCartItems;
	}
	public String getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(String subTotal) {
		this.subTotal = subTotal;
	}
	public List<OrderTotal> getTotals() {
		return totals;
	}
	public void setTotals(List<OrderTotal> totals) {
		this.totals = totals;
	}
	public List<ShoppingCartItem> getUnavailables() {
		return unavailables;
	}
	public void setUnavailables(List<ShoppingCartItem> unavailables) {
		this.unavailables = unavailables;
	}



}
