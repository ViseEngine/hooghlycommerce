package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.Optin;

public interface OptinRepository extends JpaRepository<Optin, Long> {

	@Query("select distinct o from Optin as o  left join fetch o.merchant om where om.id = ?1")
	List<Optin> findByMerchant(Integer storeId);
}
