package co.hooghly.commerce.facade;

import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductCriteria;
import co.hooghly.commerce.util.ImageFilePath;
import co.hooghly.commerce.web.populator.ReadableProductPopulator;
import co.hooghly.commerce.web.ui.ReadableProduct;
import co.hooghly.commerce.web.ui.ReadableProductList;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

import javax.inject.Inject;

@Component
public class ProductItemsFacade  {
	
	
	@Inject
	ProductService productService;
	
	@Inject
	PricingService pricingService;
	
	//@Inject
	//@Qualifier("img")
	private ImageFilePath imageUtils;

	
	public ReadableProductList listItemsByManufacturer(MerchantStore store,
			Language language, Long manufacturerId, int startCount, int maxCount) throws Exception {
		
		
		ProductCriteria productCriteria = new ProductCriteria();
		productCriteria.setMaxCount(maxCount);
		productCriteria.setStartIndex(startCount);
		

		productCriteria.setManufacturerId(manufacturerId);
		co.hooghly.commerce.domain.ProductList products = productService.listByStore(store, language, productCriteria);

		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		
		
		ReadableProductList productList = new ReadableProductList();
		for(Product product : products.getProducts()) {

			//create new proxy product
			ReadableProduct readProduct = populator.populate(product, new ReadableProduct(), store, language);
			productList.getProducts().add(readProduct);
			
		}
		
		productList.setTotalCount(products.getTotalCount());
		
		
		return productList;
	}
	
	
	public ReadableProductList listItemsByIds(MerchantStore store, Language language, List<Long> ids, int startCount,
			int maxCount) throws Exception {
		
		
		ProductCriteria productCriteria = new ProductCriteria();
		productCriteria.setMaxCount(maxCount);
		productCriteria.setStartIndex(startCount);
		productCriteria.setProductIds(ids);
		

		co.hooghly.commerce.domain.ProductList products = productService.listByStore(store, language, productCriteria);

		
		ReadableProductPopulator populator = new ReadableProductPopulator();
		populator.setPricingService(pricingService);
		populator.setimageUtils(imageUtils);
		
		
		ReadableProductList productList = new ReadableProductList();
		for(Product product : products.getProducts()) {

			//create new proxy product
			ReadableProduct readProduct = populator.populate(product, new ReadableProduct(), store, language);
			productList.getProducts().add(readProduct);
			
		}
		
		productList.setTotalCount(products.getTotalCount());
		
		
		return productList;
	}

}
