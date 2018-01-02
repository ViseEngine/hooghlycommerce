package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.CustomerOption;

public interface CustomerOptionRepository extends JpaRepository<CustomerOption, Long> {

	
	@Query("select o from CustomerOption o join fetch o.merchantStore om left join fetch o.descriptions od where o.id = ?1")
	CustomerOption findOne(Long id);
	
	@Query("select o from CustomerOption o join fetch o.merchantStore om left join fetch o.descriptions od where om.id = ?1 and o.code = ?2")
	CustomerOption findByCode(Long merchantId, String code);
	
	@Query("select o from CustomerOption o join fetch o.merchantStore om left join fetch o.descriptions od where om.id = ?1 and od.language.id = ?2")
	List<CustomerOption> findByStore(Long merchantId, Long languageId);

}
