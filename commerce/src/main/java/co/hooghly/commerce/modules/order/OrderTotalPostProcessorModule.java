package co.hooghly.commerce.modules.order;



import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.OrderSummary;
import co.hooghly.commerce.domain.OrderTotal;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ShoppingCartItem;
import co.hooghly.commerce.modules.Module;

/**
 * Calculates order total based on specific
 * modules implementation
 * 
 *
 */
public interface OrderTotalPostProcessorModule extends Module {
	
	   /**
	    * Uses the OrderSummary and external tools for applying if necessary
	    * variations on the OrderTotal calculation.
	    * @param orderSummary
	    * @param shoppingCartItem
	    * @param product
	    * @param customer
	    * @param store
	    * @return
	    * @throws Exception
	    */
	   OrderTotal caculateProductPiceVariation(final OrderSummary summary, final ShoppingCartItem shoppingCartItem, final Product product, final Customer customer, final MerchantStore store) throws Exception;

}
