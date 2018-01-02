package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.ProductOptionValue;

public interface ProductOptionValueRepository extends JpaRepository<ProductOptionValue, Long> {

	@Query("select p from ProductOptionValue p join fetch p.merchantStore pm left join fetch p.descriptions pd where p.id = ?1")
	ProductOptionValue findOne(Long id);
	
	@Query("select p from ProductOptionValue p join fetch p.merchantStore pm left join fetch p.descriptions pd where p.id = ?2  and pm.id = ?1")
	ProductOptionValue findOne(Long storeId, Long id);
	
	@Query("select distinct p from ProductOptionValue p join fetch p.merchantStore pm left join fetch p.descriptions pd where pm.id = ?1 and pd.language.id = ?2")
	List<ProductOptionValue> findByStoreId(Long storeId, Long languageId);
	
	@Query("select p from ProductOptionValue p join fetch p.merchantStore pm left join fetch p.descriptions pd where pm.id = ?1 and p.code = ?2")
	public ProductOptionValue findByCode(Long storeId, String optionValueCode);
	
	@Query("select p from ProductOptionValue p join fetch p.merchantStore pm left join fetch p.descriptions pd where pm.id = ?1 and pd.name like %?2% and pd.language.id = ?3")
	public List<ProductOptionValue> findByName(Long storeId, String name, Long languageId);
	
	
	@Query("select distinct p from ProductOptionValue p join fetch p.merchantStore pm left join fetch p.descriptions pd where pm.id = ?1 and pd.language.id = ?2 and p.productOptionDisplayOnly = ?3")
	public List<ProductOptionValue> findByReadOnly(Long storeId, Long languageId, boolean readOnly);
	

}
