package co.hooghly.commerce.web.populator;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang.Validate;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductReview;
import co.hooghly.commerce.domain.ProductReviewDescription;
import co.hooghly.commerce.util.DateUtil;
import co.hooghly.commerce.web.ui.PersistableProductReview;



public class PersistableProductReviewPopulator extends
		AbstractDataPopulator<PersistableProductReview, ProductReview> {
	
	
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private LanguageService languageService;
	


	public LanguageService getLanguageService() {
		return languageService;
	}

	public void setLanguageService(LanguageService languageService) {
		this.languageService = languageService;
	}

	@Override
	public ProductReview populate(PersistableProductReview source,
			ProductReview target, MerchantStore store, Language language)
			throws ConversionException {
		
		
		Validate.notNull(customerService,"customerService cannot be null");
		Validate.notNull(productService,"productService cannot be null");
		Validate.notNull(languageService,"languageService cannot be null");
		
		try {
			
			if(target==null) {
				target = new ProductReview();
			}
			
			Customer customer = customerService.getById(source.getCustomerId());
			
			//check if customer belongs to store
			if(customer ==null || customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Invalid customer id for the given store");
			}
			
			target.setReviewDate(DateUtil.getDate(source.getDate()));
			target.setCustomer(customer);
			target.setReviewRating(source.getRating());
			
			Product product = productService.findOne(source.getProductId());
			
			//check if product belongs to store
			if(product ==null || product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				throw new ConversionException("Invalid product id for the given store");
			}
			
			target.setProduct(product);
			
			Language lang = languageService.getByCode(language.getCode());
			if(lang ==null) {
				throw new ConversionException("Invalid language code, use iso codes (en, fr ...)");
			}
			
			ProductReviewDescription description = new ProductReviewDescription();
			description.setDescription(source.getDescription());
			description.setLanguage(lang);
			description.setName("-");
			description.setProductReview(target);
			
			Set<ProductReviewDescription> descriptions = new HashSet<ProductReviewDescription>();
			descriptions.add(description);
			
			target.setDescriptions(descriptions);
			
			

			
			
			return target;
			
		} catch (Exception e) {
			throw new ConversionException("Cannot populate ProductReview", e);
		}
		
	}

	@Override
	protected ProductReview createTarget() {
		return null;
	}
	
	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	
	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}


}
