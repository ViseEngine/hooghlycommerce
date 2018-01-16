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
 * 
 */
@Service
@Slf4j
public class PricingService {

	@Autowired
	private ProductPriceUtils priceUtil;

	public FinalPrice calculateProductPrice(Product product) {
		return priceUtil.getFinalPrice(product);
	}

	public FinalPrice calculateProductPrice(Product product, Customer customer) {
		/** TODO add rules for price calculation **/
		return priceUtil.getFinalPrice(product);
	}

	public FinalPrice calculateProductPrice(Product product, List<ProductAttribute> attributes) {
		return priceUtil.getFinalProductPrice(product, attributes);
	}

	public FinalPrice calculateProductPrice(Product product, List<ProductAttribute> attributes, Customer customer) {
		/** TODO add rules for price calculation **/
		return priceUtil.getFinalProductPrice(product, attributes);
	}

	public String getDisplayAmount(BigDecimal amount, MerchantStore store) {
		return priceUtil.getStoreFormatedAmountWithCurrency(store, amount);
	}

	public String getDisplayAmount(BigDecimal amount, Locale locale, Currency currency, MerchantStore store) {
		return priceUtil.getFormatedAmountWithCurrency(locale, currency, amount);
	}

	public String getStringAmount(BigDecimal amount, MerchantStore store) {
		return priceUtil.getAdminFormatedAmount(store, amount);

	}

}
