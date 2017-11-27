package co.hooghly.commerce.modules.shipping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.hooghly.commerce.business.MerchantConfigurationService;
import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.business.utils.ProductPriceUtils;
import co.hooghly.commerce.domain.CustomIntegrationConfiguration;
import co.hooghly.commerce.domain.Delivery;
import co.hooghly.commerce.domain.IntegrationConfiguration;
import co.hooghly.commerce.domain.IntegrationModule;
import co.hooghly.commerce.domain.MerchantConfiguration;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.PackageDetails;
import co.hooghly.commerce.domain.ShippingBasisType;
import co.hooghly.commerce.domain.ShippingConfiguration;
import co.hooghly.commerce.domain.ShippingOption;
import co.hooghly.commerce.domain.ShippingOrigin;
import co.hooghly.commerce.domain.ShippingQuote;
import co.hooghly.commerce.modules.IntegrationException;


public class CustomWeightBasedShippingQuote implements ShippingQuoteModule {
	
	public final static String MODULE_CODE = "weightBased";
	private final static String CUSTOM_WEIGHT = "CUSTOM_WEIGHT";
	
	@Autowired
	private MerchantConfigurationService merchantConfigurationService;
	
	@Autowired
	private ProductPriceUtils productPriceUtils;


	
	public void validateModuleConfiguration(
			IntegrationConfiguration integrationConfiguration,
			MerchantStore store) throws IntegrationException {
		
		
		//not used, it has its own controller with complex validators

	}
	

	
	public CustomIntegrationConfiguration getCustomModuleConfiguration(
			MerchantStore store) throws IntegrationException {

		try {

			MerchantConfiguration configuration = merchantConfigurationService.getMerchantConfiguration(MODULE_CODE, store);
	
			if(configuration!=null) {
				String value = configuration.getValue();
				ObjectMapper mapper = new ObjectMapper();
				try {
					CustomShippingQuotesConfiguration config = mapper.readValue(value, CustomShippingQuotesConfiguration.class);
					return config;
				} catch(Exception e) {
					throw new ServiceException("Cannot parse json string " + value);
				}
	
			} else {
				CustomShippingQuotesConfiguration custom = new CustomShippingQuotesConfiguration();
				custom.setModuleCode(MODULE_CODE);
				return custom;
			}
		
		} catch (Exception e) {
			throw new IntegrationException(e);
		}
		
		
	}

	
	public List<ShippingOption> getShippingQuotes(
			ShippingQuote shippingQuote,
			List<PackageDetails> packages, BigDecimal orderTotal,
			Delivery delivery, ShippingOrigin origin, MerchantStore store,
			IntegrationConfiguration configuration, IntegrationModule module,
			ShippingConfiguration shippingConfiguration, Locale locale)
			throws IntegrationException {

		if(StringUtils.isBlank(delivery.getPostalCode())) {
			return null;
		}
		
		//get configuration
		CustomShippingQuotesConfiguration customConfiguration = (CustomShippingQuotesConfiguration)this.getCustomModuleConfiguration(store);
		
		
		List<CustomShippingQuotesRegion> regions = customConfiguration.getRegions();
		
		ShippingBasisType shippingType =  shippingConfiguration.getShippingBasisType();
		ShippingOption shippingOption = null;
		try {
			

			for(CustomShippingQuotesRegion region : customConfiguration.getRegions()) {
	
				for(String countryCode : region.getCountries()) {
					if(countryCode.equals(delivery.getCountry().getIsoCode())) {
						
						
						//determine shipping weight
						double weight = 0;
						for(PackageDetails packageDetail : packages) {
							weight = weight + packageDetail.getShippingWeight();
						}
						
						//see the price associated with the width
						List<CustomShippingQuoteWeightItem> quoteItems = region.getQuoteItems();
						for(CustomShippingQuoteWeightItem quoteItem : quoteItems) {
							if(weight<= quoteItem.getMaximumWeight()) {
								shippingOption = new ShippingOption();
								shippingOption.setOptionCode(new StringBuilder().append(CUSTOM_WEIGHT).toString());
								shippingOption.setOptionId(new StringBuilder().append(CUSTOM_WEIGHT).append("_").append(region.getCustomRegionName()).toString());
								shippingOption.setOptionPrice(quoteItem.getPrice());
								shippingOption.setOptionPriceText(productPriceUtils.getStoreFormatedAmountWithCurrency(store, quoteItem.getPrice()));
								break;
							}
						}
						
					}
					
					
				}
				
			}
			
			if(shippingOption!=null) {
				List<ShippingOption> options = new ArrayList<ShippingOption>();
				options.add(shippingOption);
				return options;
			}
			
			return null;
		
		} catch (Exception e) {
			throw new IntegrationException(e);
		}

	}



}
