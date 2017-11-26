package co.hooghly.commerce.repository;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.OrderCriteria;
import co.hooghly.commerce.domain.OrderList;




public interface OrderRepositoryCustom {

	OrderList listByStore(MerchantStore store, OrderCriteria criteria);


}
