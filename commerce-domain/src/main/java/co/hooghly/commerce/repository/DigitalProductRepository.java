package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.DigitalProduct;

public interface DigitalProductRepository extends JpaRepository<DigitalProduct, Long> {

	@Query("select p from DigitalProduct p inner join fetch p.product pp inner join fetch pp.merchantStore ppm where ppm.id =?1 and pp.id = ?2")
	DigitalProduct findByProduct(Long storeId, Long productId);
	
	@Query("select p from DigitalProduct p inner join fetch p.product pp inner join fetch pp.merchantStore ppm where p.id = ?1")
	DigitalProduct findOne(Long id);
	
	
}
