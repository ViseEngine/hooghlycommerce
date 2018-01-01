package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.MerchantStore;

public interface CategoryRepository extends JpaRepository<Category, Long>, CategoryRepositoryCustom {

	List<Category> findByMerchantStoreIdAndSeUrlLikeOrderBySortOrderAsc(Long storeId, String friendlyUrl);

	Category findByMerchantStoreIdAndSeUrl(Long storeId, String friendlyUrl);

	List<Category> findByMerchantStoreIdAndNameLikeOrderBySortOrderAsc(Long storeId, String name);

	Category findByMerchantStoreIdAndCode(Long storeId, String code);

	List<Category> findByMerchantStoreIdAndCodeInOrderBySortOrderAsc(Long storeId, List<String> codes);

	List<Category> findByMerchantStoreIdAndIdInOrderBySortOrderAsc(Long storeId, List<Long> ids);

	Category findByMerchantStoreCodeAndCode(String merchantStoreCode, String code);

	List<Category> findByMerchantStoreIdAndLineageLikeOrderByLineageAscSortOrderAsc(Long merchantId, String linenage);

	List<Category> findByMerchantStoreCodeAndLineageLikeOrderByLineageAscSortOrderAsc(String storeCode, String linenage);

	List<Category> findByMerchantStoreIdAndDepthGreaterThanOrderByLineageAscSortOrderAsc(Long merchantId, int depth);

	List<Category> findByMerchantStoreIdAndDepthOrderByLineageAscSortOrderAsc(Long merchantId, int depth);

	List<Category> findByParentOrderByLineageAscSortOrderAsc(Category parent);

	List<Category> findByMerchantStoreOrderByLineageAscSortOrderAsc(MerchantStore store);

	List<Category> findByMerchantStoreAndParent(MerchantStore store, Category parent);

}
