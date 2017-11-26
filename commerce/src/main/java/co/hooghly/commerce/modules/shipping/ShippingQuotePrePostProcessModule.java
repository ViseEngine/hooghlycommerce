package co.hooghly.commerce.modules.shipping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import co.hooghly.commerce.domain.Delivery;
import co.hooghly.commerce.domain.IntegrationConfiguration;
import co.hooghly.commerce.domain.IntegrationModule;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.PackageDetails;
import co.hooghly.commerce.domain.ShippingConfiguration;
import co.hooghly.commerce.domain.ShippingOrigin;
import co.hooghly.commerce.domain.ShippingQuote;
import co.hooghly.commerce.modules.IntegrationException;

/**
 * Invoked before or after quote processing
 *
 */
@Component
public interface ShippingQuotePrePostProcessModule {
	
	
	public String getModuleCode();
	

	public void prePostProcessShippingQuotes(
			ShippingQuote quote, 
			List<PackageDetails> packages, 
			BigDecimal orderTotal, 
			Delivery delivery, 
			ShippingOrigin origin, 
			MerchantStore store, 
			IntegrationConfiguration globalShippingConfiguration, 
			IntegrationModule currentModule, 
			ShippingConfiguration shippingConfiguration, 
			List<IntegrationModule> allModules, Locale locale) throws IntegrationException;

}
