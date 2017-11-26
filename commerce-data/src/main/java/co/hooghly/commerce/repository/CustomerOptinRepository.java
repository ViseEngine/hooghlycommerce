package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.CustomerOptin;

public interface CustomerOptinRepository extends JpaRepository<CustomerOptin, Long> {

	@Query("select distinct c from CustomerOptin as c left join fetch c.optin o join fetch o.merchant om where om.id = ?1 and o.code = ?2")
	List<CustomerOptin> findByCode(Integer storeId, String code);
}
