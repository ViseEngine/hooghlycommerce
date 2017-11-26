package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.OrderProduct;

public interface OrderProductRepository extends JpaRepository<OrderProduct, Long> {


}
