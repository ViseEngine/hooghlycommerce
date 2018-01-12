package co.hooghly.commerce.business;

import java.util.ArrayList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.OrderSummary;
import co.hooghly.commerce.domain.OrderTotal;
import co.hooghly.commerce.domain.OrderTotalVariation;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.RebatesOrderTotalVariation;
import co.hooghly.commerce.domain.ShoppingCartItem;
import co.hooghly.commerce.modules.order.OrderTotalPostProcessorModule;

@Service
public class OrderTotalService  {
	
	@Autowired
	//@Resource(name="orderTotalsPostProcessors")
	List<OrderTotalPostProcessorModule> orderTotalPostProcessors;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private LanguageService languageService;

	
	public OrderTotalVariation findOrderTotalVariation(OrderSummary summary, Customer customer, MerchantStore store, Language language)
			throws Exception {
	
		RebatesOrderTotalVariation variation = new RebatesOrderTotalVariation();
		
		List<OrderTotal> totals = null;
		
		if(orderTotalPostProcessors != null) {
			for(OrderTotalPostProcessorModule module : orderTotalPostProcessors) {
				//TODO check if the module is enabled from the Admin
				
				List<ShoppingCartItem> items = summary.getProducts();
				for(ShoppingCartItem item : items) {
					
					Long productId = item.getProductId();
					Product product = productService.getProductForLocale(productId, language, languageService.toLocale(language));
					
					OrderTotal orderTotal = module.caculateProductPiceVariation(summary, item, product, customer, store);
					if(orderTotal==null) {
						continue;
					}
					if(totals==null) {
						totals = new ArrayList<OrderTotal>();
						variation.setVariations(totals);
					}
					
					//if product is null it will be catched when invoking the module
					orderTotal.setText(product.getName());
					variation.getVariations().add(orderTotal);	
				}
			}
		}
		
		
		return variation;
	}

}
