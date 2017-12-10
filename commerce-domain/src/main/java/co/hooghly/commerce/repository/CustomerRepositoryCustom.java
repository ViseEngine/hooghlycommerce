package co.hooghly.commerce.repository;

import co.hooghly.commerce.domain.CustomerCriteria;
import co.hooghly.commerce.domain.CustomerList;
import co.hooghly.commerce.domain.MerchantStore;



public interface CustomerRepositoryCustom {

	CustomerList listByStore(MerchantStore store, CustomerCriteria criteria);
	

}
