package co.hooghly.commerce.modules.shipping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import co.hooghly.commerce.business.MerchantConfigurationService;
import co.hooghly.commerce.business.utils.ProductPriceUtils;
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


/**
 * Store pick up shipping module
 * 
 * Requires a configuration of a message note to be printed to the client
 * and a price for calculation (should be configured to 0)
 * 
 * Calculates a ShippingQuote with a price set to the price configured
 *
 */
@Component
public class StorePickupShippingQuote implements ShippingQuoteModule, ShippingQuotePrePostProcessModule {
	
	
	public final static String MODULE_CODE = "storePickUp";

	@Autowired
	private MerchantConfigurationService merchantConfigurationService;
	
	@Autowired
	private ProductPriceUtils productPriceUtils;


	@Override
	public void validateModuleConfiguration(
			IntegrationConfiguration integrationConfiguration,
			MerchantStore store) throws IntegrationException {
		
		
		
		
		List<String> errorFields = null;
		
		//validate integrationKeys['account']
		Map<String,String> keys = integrationConfiguration.getIntegrationKeys();
		//if(keys==null || StringUtils.isBlank(keys.get("price"))) {
		if(keys==null) {
			errorFields = new ArrayList<String>();
			errorFields.add("price");
		} else {
			//validate it can be parsed to BigDecimal
			try {
				BigDecimal price = new BigDecimal(keys.get("price"));
			} catch(Exception e) {
				errorFields = new ArrayList<String>();
				errorFields.add("price");
			}
		}
		
		//if(keys==null || StringUtils.isBlank(keys.get("note"))) {
		if(keys==null) {
			errorFields = new ArrayList<String>();
			errorFields.add("note");
		}


		
		if(errorFields!=null) {
			IntegrationException ex = new IntegrationException(IntegrationException.ERROR_VALIDATION_SAVE);
			ex.setErrorFields(errorFields);
			throw ex;
			
		}

	}

	@Override
	public List<ShippingOption> getShippingQuotes(
			ShippingQuote shippingQuote,
			List<PackageDetails> packages, BigDecimal orderTotal,
			Delivery delivery, ShippingOrigin origin, MerchantStore store,
			IntegrationConfiguration configuration, IntegrationModule module,
			ShippingConfiguration shippingConfiguration, Locale locale)
			throws IntegrationException {

		// TODO Auto-generated method stub
		return null;

	}

	@Override
	public CustomIntegrationConfiguration getCustomModuleConfiguration(
			MerchantStore store) throws IntegrationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void prePostProcessShippingQuotes(ShippingQuote quote,
			List<PackageDetails> packages, BigDecimal orderTotal,
			Delivery delivery, ShippingOrigin origin, MerchantStore store,
			IntegrationConfiguration globalShippingConfiguration,
			IntegrationModule currentModule,
			ShippingConfiguration shippingConfiguration,
			List<IntegrationModule> allModules, Locale locale)
			throws IntegrationException {
		
		Assert.notNull(globalShippingConfiguration, "IntegrationConfiguration must not be null for StorePickUp");
		
		
		try {

			String region = null;
			
			String price = globalShippingConfiguration.getIntegrationKeys().get("price");
	
	
			if(delivery.getZone()!=null) {
				region = delivery.getZone().getCode();
			} else {
				region = delivery.getState();
			}
			
			ShippingOption shippingOption = new ShippingOption();
			shippingOption.setShippingModuleCode(MODULE_CODE);
			shippingOption.setOptionCode(MODULE_CODE);
			shippingOption.setOptionId(new StringBuilder().append(MODULE_CODE).append("_").append(region).toString());
			
			shippingOption.setOptionPrice(productPriceUtils.getAmount(price));
	
			shippingOption.setOptionPriceText(productPriceUtils.getStoreFormatedAmountWithCurrency(store, productPriceUtils.getAmount(price)));
	
			List<ShippingOption> options = quote.getShippingOptions();
			
			if(options == null) {
				options = new ArrayList<ShippingOption>();
				quote.setShippingOptions(options);
			}

			options.add(shippingOption);
			
			if(quote.getSelectedShippingOption()==null) {
				quote.setSelectedShippingOption(shippingOption);
			}

		
		} catch (Exception e) {
			throw new IntegrationException(e);
		}
	
		
		
	}

	@Override
	public String getModuleCode() {
		// TODO Auto-generated method stub
		return MODULE_CODE;
	}



}
