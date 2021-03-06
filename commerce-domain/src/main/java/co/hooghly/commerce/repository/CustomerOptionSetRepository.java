package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.CustomerOptionSet;

public interface CustomerOptionSetRepository extends JpaRepository<CustomerOptionSet, Long> {

	
	@Query("select c from CustomerOptionSet c join fetch c.customerOption co join fetch c.customerOptionValue cov join fetch co.merchantStore com left join fetch co.descriptions cod left join fetch cov.descriptions covd where c.id = ?1")
	CustomerOptionSet findOne(Long id);
	
	@Query("select c from CustomerOptionSet c join fetch c.customerOption co join fetch c.customerOptionValue cov join fetch co.merchantStore com left join fetch co.descriptions cod left join fetch cov.descriptions covd where com.id = ?1 and co.id = ?2")
	List<CustomerOptionSet> findByOptionId(Long merchantStoreId, Long id);
	
	@Query("select c from CustomerOptionSet c join fetch c.customerOption co join fetch c.customerOptionValue cov join fetch co.merchantStore com left join fetch co.descriptions cod left join fetch cov.descriptions covd where com.id = ?1 and cov.id = ?2")
	List<CustomerOptionSet> findByOptionValueId(Long merchantStoreId, Long id);
	
	@Query("select c from CustomerOptionSet c join fetch c.customerOption co join fetch c.customerOptionValue cov join fetch co.merchantStore com left join fetch co.descriptions cod left join fetch cov.descriptions covd where com.id = ?1 and cod.language.id = ?2 and covd.language.id = ?2 order by c.sortOrder asc")
	List<CustomerOptionSet> findByStore(Long merchantStoreId, Long languageId);

}
