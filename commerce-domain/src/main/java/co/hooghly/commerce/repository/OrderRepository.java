package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.Order;

public interface OrderRepository extends JpaRepository<Order, Long>, OrderRepositoryCustom {

    @Query("select o from Order o join fetch o.orderProducts op join fetch o.orderTotal ot left join fetch o.orderHistory oh left join fetch op.downloads opd left join fetch op.orderAttributes opa left join fetch op.prices opp where o.id = ?1")
	Order findOne(Long id);
}
