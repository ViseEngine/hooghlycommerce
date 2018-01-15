package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import co.hooghly.commerce.domain.ProductImage;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

	
	
}
