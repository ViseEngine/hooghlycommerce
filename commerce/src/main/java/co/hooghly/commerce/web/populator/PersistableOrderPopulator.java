package co.hooghly.commerce.web.populator;

import co.hooghly.commerce.business.ConversionException;
import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.CurrencyService;
import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.business.ProductAttributeService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.business.utils.AbstractDataPopulator;
import co.hooghly.commerce.business.utils.CreditCardUtils;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.CreditCard;
import co.hooghly.commerce.domain.Currency;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Order;
import co.hooghly.commerce.domain.OrderProduct;
import co.hooghly.commerce.domain.OrderStatus;
import co.hooghly.commerce.domain.OrderStatusHistory;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.business.DigitalProductService;

import co.hooghly.commerce.web.ui.OrderTotal;
import co.hooghly.commerce.web.ui.PersistableCustomer;
import co.hooghly.commerce.web.ui.PersistableOrder;
import co.hooghly.commerce.web.ui.PersistableOrderProduct;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class PersistableOrderPopulator extends
		AbstractDataPopulator<PersistableOrder, Order> {
	
	private CustomerService customerService;
	private CountryService countryService;
	private CurrencyService currencyService;


	private ZoneService zoneService;
	private ProductService productService;
	private DigitalProductService digitalProductService;
	private ProductAttributeService productAttributeService;

	@Override
	public Order populate(PersistableOrder source, Order target,
			MerchantStore store, Language language) throws ConversionException {
		
		
		Validate.notNull(productService,"productService must be set");
		Validate.notNull(digitalProductService,"digitalProductService must be set");
		Validate.notNull(productAttributeService,"productAttributeService must be set");
		Validate.notNull(customerService,"customerService must be set");
		Validate.notNull(countryService,"countryService must be set");
		Validate.notNull(zoneService,"zoneService must be set");
		Validate.notNull(currencyService,"currencyService must be set");

		try {
			

			Map<String,Country> countriesMap = countryService.getCountriesMap(language);
			Map<String,Zone> zonesMap = zoneService.getZones(language);
			/** customer **/
			PersistableCustomer customer = source.getCustomer();
			if(customer!=null) {
				if(customer.getId()!=null && customer.getId()>0) {
					Customer modelCustomer = customerService.getById(customer.getId());
					if(modelCustomer==null) {
						throw new ConversionException("Customer id " + customer.getId() + " does not exists");
					}
					if(modelCustomer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
						throw new ConversionException("Customer id " + customer.getId() + " does not exists for store " + store.getCode());
					}
					target.setCustomerId(modelCustomer.getId());
					target.setBilling(modelCustomer.getBilling());
					target.setDelivery(modelCustomer.getDelivery());
					target.setCustomerEmailAddress(source.getCustomer().getEmailAddress());


					
				} 
			}
			
			//target.setLocale(LocaleUtils.getLocale(store));
			
			CreditCard creditCard = source.getCreditCard();
			if(creditCard!=null) {
				String maskedNumber = CreditCardUtils.maskCardNumber(creditCard.getCcNumber());
				creditCard.setCcNumber(maskedNumber);
				target.setCreditCard(creditCard);
			}
			
			Currency currency = null;
			try {
				currency = currencyService.getByCode(source.getCurrency());
			} catch(Exception e) {
				throw new ConversionException("Currency not found for code " + source.getCurrency());
			}
			
			if(currency==null) {
				throw new ConversionException("Currency not found for code " + source.getCurrency());
			}
			
			target.setCurrency(currency);
			target.setDatePurchased(source.getDatePurchased());
			//target.setCurrency(store.getCurrency());
			target.setCurrencyValue(new BigDecimal(0));
			target.setMerchant(store);
			target.setStatus(source.getOrderStatus());
			target.setPaymentModuleCode(source.getPaymentModule());
			target.setPaymentType(source.getPaymentType());
			target.setShippingModuleCode(source.getShippingModule());
			target.setCustomerAgreement(source.isCustomerAgreed());
			target.setConfirmedAddress(source.isConfirmedAddress());
			if(source.getPreviousOrderStatus()!=null) {
				List<OrderStatus> orderStatusList = source.getPreviousOrderStatus();
				for(OrderStatus status : orderStatusList) {
					OrderStatusHistory statusHistory = new OrderStatusHistory();
					statusHistory.setStatus(status);
					statusHistory.setOrder(target);
					target.getOrderHistory().add(statusHistory);
				}
			}
			
			List<PersistableOrderProduct> products = source.getOrderProductItems();
			if(CollectionUtils.isEmpty(products)) {
				throw new ConversionException("Requires at least 1 PersistableOrderProduct");
			}
			co.hooghly.commerce.web.populator.PersistableOrderProductPopulator orderProductPopulator = new PersistableOrderProductPopulator();
			orderProductPopulator.setProductAttributeService(productAttributeService);
			orderProductPopulator.setProductService(productService);
			//orderProductPopulator.setDigitalProductService(digitalProductService);
			
			for(PersistableOrderProduct orderProduct : products) {
				OrderProduct modelOrderProduct = new OrderProduct();
				orderProductPopulator.populate(orderProduct, modelOrderProduct, store, language);
				target.getOrderProducts().add(modelOrderProduct);
			}
			
			List<OrderTotal> orderTotals = source.getTotals();
			if(CollectionUtils.isNotEmpty(orderTotals)) {
				for(OrderTotal total : orderTotals) {
					co.hooghly.commerce.domain.OrderTotal totalModel = new co.hooghly.commerce.domain.OrderTotal();
					totalModel.setModule(total.getModule());
					totalModel.setOrder(target);
					totalModel.setOrderTotalCode(total.getCode());
					totalModel.setTitle(total.getTitle());
					totalModel.setValue(total.getValue());
					target.getOrderTotal().add(totalModel);
				}
			}
			
		} catch (Exception e) {
			throw new ConversionException(e);
		}
		
		
		return target;
	}

	@Override
	protected Order createTarget() {
		return null;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setDigitalProductService(DigitalProductService digitalProductService) {
		this.digitalProductService = digitalProductService;
	}

	public DigitalProductService getDigitalProductService() {
		return digitalProductService;
	}

	public void setProductAttributeService(ProductAttributeService productAttributeService) {
		this.productAttributeService = productAttributeService;
	}

	public ProductAttributeService getProductAttributeService() {
		return productAttributeService;
	}
	
	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CountryService getCountryService() {
		return countryService;
	}

	public void setCountryService(CountryService countryService) {
		this.countryService = countryService;
	}

	public CurrencyService getCurrencyService() {
		return currencyService;
	}

	public void setCurrencyService(CurrencyService currencyService) {
		this.currencyService = currencyService;
	}

	public ZoneService getZoneService() {
		return zoneService;
	}

	public void setZoneService(ZoneService zoneService) {
		this.zoneService = zoneService;
	}

}
