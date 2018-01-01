package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.MerchantStore;

public interface MerchantStoreRepository extends JpaRepository<MerchantStore, Long> {

	MerchantStore findByCode(String code);
}
