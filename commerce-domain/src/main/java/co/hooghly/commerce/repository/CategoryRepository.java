package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.MerchantStore;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

	List<Category> findByMerchantStoreIdAndSeUrlLikeOrderBySortOrderAsc(Integer storeId, String friendlyUrl);

	Category findByMerchantStoreIdAndSeUrl(Integer storeId, String friendlyUrl);

	List<Category> findByMerchantStoreIdAndNameLikeOrderBySortOrderAsc(Integer storeId, String name);

	Category findByMerchantStoreIdAndCode(Integer storeId, String code);

	List<Category> findByMerchantStoreIdAndCodeInOrderBySortOrderAsc(Integer storeId, List<String> codes);

	List<Category> findByMerchantStoreIdAndIdInOrderBySortOrderAsc(Integer storeId, List<Long> ids);

	Category findByMerchantStoreCodeAndCode(String merchantStoreCode, String code);

	List<Category> findByMerchantStoreIdAndLineageLikeOrderByLineageAscSortOrderAsc(Integer merchantId, String linenage);

	List<Category> findByMerchantStoreCodeAndLineageLikeOrderByLineageAscSortOrderAsc(String storeCode, String linenage);

	List<Category> findByMerchantStoreIdAndDepthGreaterThanOrderByLineageAscSortOrderAsc(Integer merchantId, int depth);

	List<Category> findByMerchantStoreIdAndDepthOrderByLineageAscSortOrderAsc(Integer merchantId, int depth);

	List<Category> findByParentOrderByLineageAscSortOrderAsc(Category parent);

	List<Category> findByMerchantStoreOrderByLineageAscSortOrderAsc(MerchantStore store);

	List<Category> findByMerchantStoreAndParent(MerchantStore store, Category parent);

}
