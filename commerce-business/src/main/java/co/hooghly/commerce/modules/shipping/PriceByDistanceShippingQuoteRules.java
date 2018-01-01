package co.hooghly.commerce.modules.shipping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.modules.IntegrationException;


/**
 * Requires to set pre-processor distance calculator
 * pre-processor calculates the distance (in kilometers [can be changed to miles]) based on delivery address
 * when that module is invoked during process it will calculate the price
 * DISTANCE * PRICE/KM
 * 
 *
 */
public class PriceByDistanceShippingQuoteRules implements ShippingQuoteModule {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PriceByDistanceShippingQuoteRules.class);

	public final static String MODULE_CODE = "priceByDistance";

	
	public void validateModuleConfiguration(
			IntegrationConfiguration integrationConfiguration,
			MerchantStore store) throws IntegrationException {
		// Not used

	}

	
	public CustomIntegrationConfiguration getCustomModuleConfiguration(
			MerchantStore store) throws IntegrationException {
		// Not used
		return null;
	}

	
	public List<ShippingOption> getShippingQuotes(ShippingQuote quote,
			List<PackageDetails> packages, BigDecimal orderTotal,
			Delivery delivery, ShippingOrigin origin, MerchantStore store,
			IntegrationConfiguration configuration, IntegrationModule module,
			ShippingConfiguration shippingConfiguration, Locale locale)
			throws IntegrationException {

		
		
		Validate.notNull(delivery, "Delivery cannot be null");
		Validate.notNull(delivery.getCountry(), "Delivery.country cannot be null");
		Validate.notNull(packages, "packages cannot be null");
		Validate.notEmpty(packages, "packages cannot be empty");
		
		//requires the postal code
		if(StringUtils.isBlank(delivery.getPostalCode())) {
			return null;
		}

		Double distance = null;
		
		if(quote!=null) {
			//look if distance has been calculated
			if(quote.getQuoteInformations()!=null) {
				if(quote.getQuoteInformations().containsKey(Constants.DISTANCE_KEY)) {
					distance = (Double)quote.getQuoteInformations().get(Constants.DISTANCE_KEY);
				}
			}
		}
		
		if(distance==null) {
			return null;
		}
		
		//maximum distance TODO configure from admin
		if(distance > 150D) {
			return null;
		}
		
		List<ShippingOption> options = quote.getShippingOptions();
		
		if(options == null) {
			options = new ArrayList<ShippingOption>();
			quote.setShippingOptions(options);
		}
		
		BigDecimal price = null;
		BigDecimal total = null;
		
		if(distance<=20) {
			price = new BigDecimal(69);//TODO from the admin
			total = new BigDecimal(distance).multiply(price);
		} else {
			price = new BigDecimal(3);//TODO from the admin
			total = new BigDecimal(distance).multiply(price);
		}
		
		


		ShippingOption shippingOption = new ShippingOption();
			
			
		shippingOption.setOptionPrice(total);
		shippingOption.setShippingModuleCode(MODULE_CODE);
		shippingOption.setOptionCode(MODULE_CODE);
		shippingOption.setOptionId(MODULE_CODE);

		options.add(shippingOption);

		
		return options;
		
		
	}

}