package co.hooghly.commerce.repository;

import java.util.stream.Stream;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.ProductRelationship;


public interface ProductRelationshipRepository extends JpaRepository<ProductRelationship, Long>, ProductRelationshipRepositoryCustom {
	
	Stream<ProductRelationship> findByStoreAndCode(MerchantStore store, String code);
}
