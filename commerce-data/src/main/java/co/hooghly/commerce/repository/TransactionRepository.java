package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query("select t from Transaction t join fetch t.order to where to.id = ?1")
	List<Transaction> findByOrder(Long orderId);
}
