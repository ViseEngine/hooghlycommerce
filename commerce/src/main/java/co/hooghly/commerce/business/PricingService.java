package co.hooghly.commerce.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.business.utils.ProductPriceUtils;
import co.hooghly.commerce.domain.Currency;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.FinalPrice;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAttribute;
import lombok.extern.slf4j.Slf4j;

/**
 * Contains all the logic required to calculate product price
 
 *
 */
@Service
@Slf4j
public class PricingService  {
	
	

	@Autowired
	private ProductPriceUtils priceUtil;
	
	
	public FinalPrice calculateProductPrice(Product product) throws ServiceException {
		return priceUtil.getFinalPrice(product);
	}
	
	
	public FinalPrice calculateProductPrice(Product product, Customer customer) throws ServiceException {
		/** TODO add rules for price calculation **/
		return priceUtil.getFinalPrice(product);
	}
	
	
	public FinalPrice calculateProductPrice(Product product, List<ProductAttribute> attributes) throws ServiceException {
		return priceUtil.getFinalProductPrice(product, attributes);
	}
	
	
	public FinalPrice calculateProductPrice(Product product, List<ProductAttribute> attributes, Customer customer) throws ServiceException {
		/** TODO add rules for price calculation **/
		return priceUtil.getFinalProductPrice(product, attributes);
	}

	
	public String getDisplayAmount(BigDecimal amount, MerchantStore store) throws ServiceException {
		try {
			String price= priceUtil.getStoreFormatedAmountWithCurrency(store,amount);
			return price;
		} catch (Exception e) {
			log.error("An error occured when trying to format an amount " + amount.toString());
			throw new ServiceException(e);
		}
	}
	
	
	public String getDisplayAmount(BigDecimal amount, Locale locale,
			Currency currency, MerchantStore store) throws ServiceException {
		try {
			String price= priceUtil.getFormatedAmountWithCurrency(locale, currency, amount);
			return price;
		} catch (Exception e) {
			log.error("An error occured when trying to format an amunt " + amount.toString() + " using locale " + locale.toString() + " and currency " + currency.toString());
			throw new ServiceException(e);
		}
	}

	
	public String getStringAmount(BigDecimal amount, MerchantStore store)
			throws ServiceException {
		try {
			String price = priceUtil.getAdminFormatedAmount(store, amount);
			return price;
		} catch (Exception e) {
			log.error("An error occured when trying to format an amount " + amount.toString());
			throw new ServiceException(e);
		}
	}


	
}
