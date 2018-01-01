package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.CustomerOption;
import co.hooghly.commerce.domain.CustomerOptionValue;

public interface CustomerOptionValueRepository extends JpaRepository<CustomerOptionValue, Long> {

	
	@Query("select o from CustomerOptionValue o join fetch o.merchantStore om left join fetch o.descriptions od where o.id = ?1")
	CustomerOptionValue findOne(Long id);
	
	@Query("select o from CustomerOptionValue o join fetch o.merchantStore om left join fetch o.descriptions od where om.id = ?1 and o.code = ?2")
	CustomerOptionValue findByCode(Long merchantId, String code);
	
	@Query("select o from CustomerOptionValue o join fetch o.merchantStore om left join fetch o.descriptions od where om.id = ?1 and od.language.id = ?2")
	List<CustomerOptionValue> findByStore(Long merchantId, Integer languageId);

}
