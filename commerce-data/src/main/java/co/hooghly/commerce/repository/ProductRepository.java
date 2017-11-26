package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.Product;


public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

}
