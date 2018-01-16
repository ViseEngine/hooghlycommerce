package co.hooghly.commerce.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * Transient entity used to display
 * different price information in the catalogue
 * 
 *
 */
@Data
public class FinalPrice implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal discountedPrice = null;//final price if a discount is applied
	private BigDecimal originalPrice = null;//original price
	private BigDecimal finalPrice = null;//final price discount or not
	private boolean discounted = false;
	private int discountPercent = 0;
	
	private Date discountEndDate = null;
	
	private boolean defaultPrice;
	private ProductPrice productPrice;
	List<FinalPrice> additionalPrices;

	private String finalDisplayPrice;
}
