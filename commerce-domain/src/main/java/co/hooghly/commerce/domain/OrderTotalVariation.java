package co.hooghly.commerce.domain;

import java.util.List;

/**
 * Contains a list of negative OrderTotal variation
 * that will be shown in the order summary

 *
 */
public abstract class OrderTotalVariation {
	
	List<OrderTotal> variations = null;

	public List<OrderTotal> getVariations() {
		return variations;
	}

	public void setVariations(List<OrderTotal> variations) {
		this.variations = variations;
	}

}
