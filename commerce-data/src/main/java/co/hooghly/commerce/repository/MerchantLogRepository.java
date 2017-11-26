package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.MerchantLog;
import co.hooghly.commerce.domain.MerchantStore;

public interface MerchantLogRepository extends JpaRepository<MerchantLog, Long> {

	public List<MerchantLog> findByStore(MerchantStore store);
}
