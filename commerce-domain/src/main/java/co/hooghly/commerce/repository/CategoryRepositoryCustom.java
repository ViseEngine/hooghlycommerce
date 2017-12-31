package co.hooghly.commerce.repository;

import java.util.List;

import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.MerchantStore;

public interface CategoryRepositoryCustom {

	List<Object[]> countProductsByCategories(MerchantStore store,
			List<Long> categoryIds);

	List<Category> listByStoreAndParent(MerchantStore store, Category category);

}
