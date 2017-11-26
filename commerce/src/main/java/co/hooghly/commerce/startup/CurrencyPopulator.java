package co.hooghly.commerce.startup;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.CurrencyService;
import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.constants.SchemaConstant;
import co.hooghly.commerce.domain.Currency;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(2)
public class CurrencyPopulator extends AbstractDataPopulator {
	
	public CurrencyPopulator() {
		super("CURRENCY");
	}
	
	@Autowired
	private CurrencyService currencyService;

	@Override
	public void runInternal(String... args) throws Exception {
		log.info("2.Loading currencies.");
		createCurrencies();
	}
	
	private void createCurrencies() throws ServiceException {
		
		for (String code : SchemaConstant.CURRENCY_MAP.keySet()) {

		      
            try {
            	java.util.Currency c = java.util.Currency.getInstance(code);
            	
            	if(c==null) {
            		log.info("Populating Currencies : no currency for code : %s",  code);
            	}
            	
            		//check if it exist
            		
	            	Currency currency = new Currency();
	            	currency.setName(c.getCurrencyCode());
	            	currency.setCurrency(c);
	            	currencyService.create(currency);

            
            } catch (IllegalArgumentException e) {
            	log.info("Populating Currencies : no currency for code : %s", code);
            }
        }  
	}
}
