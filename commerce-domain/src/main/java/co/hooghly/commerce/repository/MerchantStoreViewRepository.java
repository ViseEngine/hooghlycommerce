package co.hooghly.commerce.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.MerchantStoreView;

public interface MerchantStoreViewRepository extends JpaRepository<MerchantStoreView, Integer> {

	Optional<MerchantStoreView> findByCode(String code);
	
}
