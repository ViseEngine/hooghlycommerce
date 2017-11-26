package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.OrderTotal;

public interface OrderTotalRepository extends JpaRepository<OrderTotal, Long> {


}
