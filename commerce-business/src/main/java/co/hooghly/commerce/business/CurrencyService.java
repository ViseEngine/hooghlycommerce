package co.hooghly.commerce.business;



import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Currency;
import co.hooghly.commerce.repository.CurrencyRepository;

@Service
public class CurrencyService extends SalesManagerEntityServiceImpl<Long, Currency>
	 {
	
	private CurrencyRepository currencyRepository;
	
	
	public CurrencyService(CurrencyRepository currencyRepository) {
		super(currencyRepository);
		this.currencyRepository = currencyRepository;
	}

	
	public Currency getByCode(String code) {
		return currencyRepository.getByCode(code);
	}

}
