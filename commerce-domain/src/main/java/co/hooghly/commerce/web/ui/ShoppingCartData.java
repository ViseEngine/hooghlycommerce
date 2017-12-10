package co.hooghly.commerce.web.ui;

import java.io.Serializable;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;


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
	
	
}
