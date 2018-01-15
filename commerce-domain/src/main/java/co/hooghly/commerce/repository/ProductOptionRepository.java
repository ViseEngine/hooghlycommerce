package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.ProductOption;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {

	@Query("select p from ProductOption p join fetch p.merchantStore pm left join fetch p.descriptions pd where p.id = ?1")
	ProductOption findOne(Long id);
	
	@Query("select p from ProductOption p join fetch p.merchantStore pm left join fetch p.descriptions pd where p.id = ?2 and pm.id = ?1")
	ProductOption findOne(Long storeId, Long id);
	
	@Query("select distinct p from ProductOption p join fetch p.merchantStore pm left join fetch p.descriptions pd where pm.id = ?1 and pd.language.id = ?2")
	List<ProductOption> findByStoreId(Long storeId, Long languageId);
	
	@Query("select p from ProductOption p join fetch p.merchantStore pm left join fetch p.descriptions pd where pm.id = ?1 and pd.name like %?2% and pd.language.id = ?3")
	public List<ProductOption> findByName(Long storeId, String name, Long languageId);
	
	@Query("select p from ProductOption p join fetch p.merchantStore pm left join fetch p.descriptions pd where pm.id = ?1 and p.code = ?2")
	public ProductOption findByCode(Long storeId, String optionCode);
	
	@Query("select distinct p from ProductOption p join fetch p.merchantStore pm left join fetch p.descriptions pd where pm.id = ?1 and p.code = ?2 and p.readOnly = ?3")
	public List<ProductOption> findByReadOnly(Long storeId, Long languageId, boolean readOnly);
	

}
