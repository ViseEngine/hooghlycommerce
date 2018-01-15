package co.hooghly.commerce.modules.order;

import java.math.BigDecimal;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatelessKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.FinalPrice;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.OrderSummary;
import co.hooghly.commerce.domain.OrderTotal;
import co.hooghly.commerce.domain.OrderTotalType;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ShoppingCartItem;




/**
 * Add variation to the OrderTotal
 * This has to be defined in hooghly-core-ordertotal-processors
 *
 */
@Component
public class ManufacturerShippingCodeOrderTotalModuleImpl implements OrderTotalPostProcessorModule {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ManufacturerShippingCodeOrderTotalModuleImpl.class);
	
	private String name;
	private String code;
	
	private StatelessKnowledgeSession orderTotalMethodDecision;//injected from xml file
	
	private KnowledgeBase kbase;//injected from xml file
	

	PricingService pricingService;
	

	
	public PricingService getPricingService() {
		return pricingService;
	}

	public void setPricingService(PricingService pricingService) {
		this.pricingService = pricingService;
	}

	@Override
	public OrderTotal caculateProductPiceVariation(final OrderSummary summary, ShoppingCartItem shoppingCartItem, Product product, Customer customer, MerchantStore store)
			throws Exception {

		
		Assert.notNull(product,"product must not be null");
		Assert.notNull(product.getManufacturer(),"product manufacturer must not be null");
		
		//requires shipping summary, otherwise return null
		if(summary.getShippingSummary()==null) {
			return null;
		}

		OrderTotalInputParameters inputParameters = new OrderTotalInputParameters();
		inputParameters.setItemManufacturerCode(product.getManufacturer().getCode());
		
		
		inputParameters.setShippingMethod(summary.getShippingSummary().getShippingOptionCode());
		
		LOGGER.debug("Setting input parameters " + inputParameters.toString());
		orderTotalMethodDecision.execute(inputParameters);
		
		
		LOGGER.debug("Applied discount " + inputParameters.getDiscount());
		
		OrderTotal orderTotal = null;
		if(inputParameters.getDiscount() != null) {
				orderTotal = new OrderTotal();
				orderTotal.setOrderTotalCode(Constants.OT_DISCOUNT_TITLE);
				orderTotal.setOrderTotalType(OrderTotalType.SUBTOTAL);
				orderTotal.setTitle(Constants.OT_SUBTOTAL_MODULE_CODE);
				
				//calculate discount that will be added as a negative value
				FinalPrice productPrice = pricingService.calculateProductPrice(product);
				
				Double discount = inputParameters.getDiscount();
				BigDecimal reduction = productPrice.getFinalPrice().multiply(new BigDecimal(discount));
				reduction = reduction.multiply(new BigDecimal(shoppingCartItem.getQuantity()));
				
				orderTotal.setValue(reduction);
		}
			
		
		
		return orderTotal;

	}
	
	public KnowledgeBase getKbase() {
		return kbase;
	}


	public void setKbase(KnowledgeBase kbase) {
		this.kbase = kbase;
	}

	public StatelessKnowledgeSession getOrderTotalMethodDecision() {
		return orderTotalMethodDecision;
	}

	public void setOrderTotalMethodDecision(StatelessKnowledgeSession orderTotalMethodDecision) {
		this.orderTotalMethodDecision = orderTotalMethodDecision;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}



}
