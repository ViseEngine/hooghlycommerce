package co.hooghly.commerce.facade;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.ManufacturerService;
import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.ProductOptionService;
import co.hooghly.commerce.business.ProductOptionValueService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.business.TaxClassService;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAvailability;
import co.hooghly.commerce.domain.ProductPrice;
import co.hooghly.commerce.util.DateUtil;
import co.hooghly.commerce.util.ImageFilePath;
import co.hooghly.commerce.web.populator.PersistableProductPopulator;
import co.hooghly.commerce.web.populator.ReadableProductPopulator;
import co.hooghly.commerce.web.ui.PersistableProduct;
import co.hooghly.commerce.web.ui.ProductPriceEntity;
import co.hooghly.commerce.web.ui.ReadableProduct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;

@Service
public class ProductFacade  {
	
	@Inject
	private CategoryService categoryService;
	
	@Inject
	private ManufacturerService manufacturerService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private ProductOptionService productOptionService;
	
	@Inject
	private ProductOptionValueService productOptionValueService;
	
	@Inject
	private TaxClassService taxClassService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private PricingService pricingService;
	
	//@Inject
	//@Qualifier("img")
	private ImageFilePath imageUtils;

	
	public PersistableProduct saveProduct(MerchantStore store, PersistableProduct product, Language language)
			throws Exception {
		
		
		PersistableProductPopulator persistableProductPopulator = new PersistableProductPopulator();
		
		persistableProductPopulator.setCategoryService(categoryService);
		persistableProductPopulator.setManufacturerService(manufacturerService);
		persistableProductPopulator.setLanguageService(languageService);
		persistableProductPopulator.setProductOptionService(productOptionService);
		persistableProductPopulator.setProductOptionValueService(productOptionValueService);
		persistableProductPopulator.setTaxClassService(taxClassService);
		
		Product target = new Product();
		
		persistableProductPopulator.populate(product, target, store, language);
		
		productService.create(target);
		
		product.setId(target.getId());
		
		return product;
		

	}

	
	public ReadableProduct getProduct(MerchantStore store, Long id, Language language)
			throws Exception {

		Product product = productService.getById(id);
		
		if(product==null) {
			return null;
		}
		
		ReadableProduct readableProduct = new ReadableProduct();
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		populator.populate(product, readableProduct, store, language);
		
		return readableProduct;
	}

	
	public ReadableProduct getProduct(MerchantStore store, String sku,
			Language language) throws Exception {
		
		Product product = productService.getByCode(sku, language);
		
		if(product==null) {
			return null;
		}
		
		ReadableProduct readableProduct = new ReadableProduct();
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		populator.populate(product, readableProduct, store, language);
		
		return readableProduct;
	}

	
	public ReadableProduct updateProductPrice(ReadableProduct product,
			ProductPriceEntity price, Language language) throws Exception {
		
		
		Product persistable = productService.getById(product.getId());
		
		if(persistable==null) {
			throw new Exception("product is null for id " + product.getId());
		}
		
		java.util.Set<ProductAvailability> availabilities = persistable.getAvailabilities();
		for(ProductAvailability availability : availabilities) {
			ProductPrice productPrice = availability.defaultPrice();
			productPrice.setProductPriceAmount(price.getOriginalPrice());
			if(price.isDiscounted()) {
				productPrice.setProductPriceSpecialAmount(price.getDiscountedPrice());
				if(!StringUtils.isBlank(price.getDiscountStartDate())) {
					Date startDate = DateUtil.getDate(price.getDiscountStartDate());
					productPrice.setProductPriceSpecialStartDate(startDate);
				}
				if(!StringUtils.isBlank(price.getDiscountEndDate())) {
					Date endDate = DateUtil.getDate(price.getDiscountEndDate());
					productPrice.setProductPriceSpecialEndDate(endDate);
				}
			}
			
		}
		
		productService.update(persistable);
		
		ReadableProduct readableProduct = new ReadableProduct();
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		populator.populate(persistable, readableProduct, persistable.getMerchantStore(), language);
		
		return readableProduct;
	}

	
	public ReadableProduct updateProductQuantity(ReadableProduct product,
			int quantity, Language language) throws Exception {
		Product persistable = productService.getById(product.getId());
		
		if(persistable==null) {
			throw new Exception("product is null for id " + product.getId());
		}
		
		java.util.Set<ProductAvailability> availabilities = persistable.getAvailabilities();
		for(ProductAvailability availability : availabilities) {
			availability.setProductQuantity(quantity);
		}
		
		productService.update(persistable);
		
		ReadableProduct readableProduct = new ReadableProduct();
		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		populator.populate(persistable, readableProduct, persistable.getMerchantStore(), language);
		
		return readableProduct;
	}

}
