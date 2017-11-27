package co.hooghly.commerce.modules.shipping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;


import co.hooghly.commerce.domain.CustomIntegrationConfiguration;
import co.hooghly.commerce.domain.Delivery;
import co.hooghly.commerce.domain.IntegrationConfiguration;
import co.hooghly.commerce.domain.IntegrationModule;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.PackageDetails;
import co.hooghly.commerce.domain.ShippingConfiguration;
import co.hooghly.commerce.domain.ShippingOption;
import co.hooghly.commerce.domain.ShippingOrigin;
import co.hooghly.commerce.domain.ShippingQuote;
import co.hooghly.commerce.modules.IntegrationException;

public interface ShippingQuoteModule {
	
	public void validateModuleConfiguration(IntegrationConfiguration integrationConfiguration, MerchantStore store) throws IntegrationException;
	public CustomIntegrationConfiguration getCustomModuleConfiguration(MerchantStore store) throws IntegrationException;
	
	public List<ShippingOption> getShippingQuotes(ShippingQuote quote, List<PackageDetails> packages, BigDecimal orderTotal, Delivery delivery, ShippingOrigin origin, MerchantStore store, IntegrationConfiguration configuration, IntegrationModule module, ShippingConfiguration shippingConfiguration, Locale locale) throws IntegrationException;

}
