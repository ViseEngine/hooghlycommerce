package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.Currency;

public interface CurrencyRepository extends JpaRepository <Currency, Long> {

	
	Currency getByCode(String code);
}
