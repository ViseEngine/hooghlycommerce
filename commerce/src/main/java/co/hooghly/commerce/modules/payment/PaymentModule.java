package co.hooghly.commerce.modules.payment;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.IntegrationConfiguration;
import co.hooghly.commerce.domain.IntegrationModule;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Order;
import co.hooghly.commerce.domain.Payment;
import co.hooghly.commerce.domain.ShoppingCartItem;
import co.hooghly.commerce.domain.Transaction;
import co.hooghly.commerce.modules.IntegrationException;

@Component
public interface PaymentModule {
	
	public void validateModuleConfiguration(IntegrationConfiguration integrationConfiguration, MerchantStore store) throws IntegrationException;
	
	/**
	 * Returns token-value related to the initialization of the transaction This
	 * method is invoked for paypal express checkout
	 * @param customer
	 * @param order
	 * @return
	 * @throws IntegrationException
	 */
	public Transaction initTransaction(
			MerchantStore store, Customer customer, BigDecimal amount, Payment payment, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException;
	
	public Transaction authorize(
			MerchantStore store, Customer customer, List<ShoppingCartItem> items, BigDecimal amount, Payment payment, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException;

	
	public Transaction capture(
			MerchantStore store, Customer customer, Order order, Transaction capturableTransaction, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException;
	
	public Transaction authorizeAndCapture(
			MerchantStore store, Customer customer, List<ShoppingCartItem> items, BigDecimal amount, Payment payment, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException;
	
	public Transaction refund(
			boolean partial, MerchantStore store, Transaction transaction, Order order, BigDecimal amount, IntegrationConfiguration configuration, IntegrationModule module)
			throws IntegrationException;

}
