package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.OrderAccount;

public interface OrderAccountRepository extends JpaRepository<OrderAccount, Long> {


}
