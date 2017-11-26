package co.hooghly.commerce.repository;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductCriteria;
import co.hooghly.commerce.domain.ProductList;
import co.hooghly.commerce.domain.TaxClass;

public interface ProductRepositoryCustom {
	
	
	
	

		ProductList listByStore(MerchantStore store, Language language,
			ProductCriteria criteria);
		
		 Product getByFriendlyUrl(MerchantStore store,String seUrl, Locale locale);

		List<Product> getProductsListByCategories(@SuppressWarnings("rawtypes") Set categoryIds);

		List<Product> getProductsListByCategories(Set<Long> categoryIds,
				Language language);

		List<Product> listByTaxClass(TaxClass taxClass);

		List<Product> listByStore(MerchantStore store);

		Product getProductForLocale(long productId, Language language,
				Locale locale);

		Product getById(Long productId);

		Product getByCode(String productCode, Language language);

		List<Product> getProductsForLocale(MerchantStore store,
				Set<Long> categoryIds, Language language, Locale locale);

}
