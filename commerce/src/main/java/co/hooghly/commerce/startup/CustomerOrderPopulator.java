package co.hooghly.commerce.startup;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.CurrencyService;
import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.business.GroupService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.OrderService;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Billing;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Currency;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.CustomerGender;
import co.hooghly.commerce.domain.Delivery;
import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Order;
import co.hooghly.commerce.domain.OrderProduct;
import co.hooghly.commerce.domain.OrderProductDownload;
import co.hooghly.commerce.domain.OrderProductPrice;
import co.hooghly.commerce.domain.OrderStatus;
import co.hooghly.commerce.domain.OrderStatusHistory;
import co.hooghly.commerce.domain.OrderTotal;
import co.hooghly.commerce.domain.PaymentType;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.util.LocaleUtils;
import lombok.extern.slf4j.Slf4j;
@Component
@Slf4j
@org.springframework.core.annotation.Order(13)
public class CustomerOrderPopulator extends AbstractDataPopulator {
	
	public CustomerOrderPopulator() {
		super("CUSTOMER-ORDER-DEMO");
	}
	
	@Autowired
	protected MerchantStoreService merchantService;
	
	@Autowired
	protected LanguageService languageService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	protected GroupService   groupService;
	
	@Autowired
	protected CountryService countryService;
	
	@Autowired
	protected ZoneService zoneService;
	
	@Autowired
	protected CustomerService customerService;
	
	@Autowired
	protected CurrencyService currencyService;
	
	@Autowired
	protected OrderService orderService;

	@Override
	public void runInternal(String... args) throws Exception {
		
		Country canada = countryService.getByCode("CA");
		Zone zone = zoneService.getByCode("QC");
		
		Language en = languageService.getByCode("en");
		Language fr = languageService.getByCode("fr");
		
		MerchantStore store = merchantService.getMerchantStore(MerchantStore.DEFAULT_STORE);
		
		//Create a customer (user name[nick] : hooghly password : password)

	    Customer customer = new Customer();
		customer.setMerchantStore(store);
		customer.setEmailAddress("test@googhly.com");
		customer.setGender(CustomerGender.M);						
		customer.setAnonymous(false);
		customer.setCompany("Hooghly Co");
		customer.setDateOfBirth(new Date());

		customer.setDefaultLanguage(en);
		customer.setNick("hooghly");
		
		String password = passwordEncoder.encode("password");
		customer.setPassword(password);
		
		List<Group> groups = groupService.listGroup(GroupType.CUSTOMER);
		  

		for(Group group : groups) {
			  if(group.getGroupName().equals(Constants.GROUP_CUSTOMER)) {
				  customer.getGroups().add(group);
			  }
		}
		
	    Delivery delivery = new Delivery();
	    delivery.setAddress("358 Du Languadoc");
	    delivery.setCity( "Boucherville" );
	    delivery.setCountry(canada);
//	    delivery.setCountryCode(canada.getIsoCode());
	    delivery.setFirstName("Leonardo" );
	    delivery.setLastName("DiCaprio" );
	    delivery.setPostalCode("J4B-8J9" );
	    delivery.setZone(zone);	    
	    
	    Billing billing = new Billing();
	    billing.setAddress("358 Du Languadoc");
	    billing.setCity("Boucherville");
	    billing.setCompany("CSTI Consulting");
	    billing.setCountry(canada);
//	    billing.setCountryCode(canada.getIsoCode());
	    billing.setFirstName("Leonardo" );
	    billing.setLastName("DiCaprio" );
	    billing.setPostalCode("J4B-8J9");
	    billing.setZone(zone);
	    
	    customer.setBilling(billing);
	    customer.setDelivery(delivery);		
		customerService.create(customer);
		
		Currency currency = currencyService.getByCode("CAD");

		OrderStatusHistory orderStatusHistory = new OrderStatusHistory();
		
		//create an order
		
		Order order = new Order();
		order.setDatePurchased(new Date());
		order.setCurrency(currency);
		order.setLastModified(new Date());
		order.setBilling(billing);

		
		order.setLocale(LocaleUtils.getLocale(store));

		order.setCurrencyValue(new BigDecimal(0.98));//compared to based currency (not necessary)
		order.setCustomerId(customer.getId());
		order.setBilling(billing);
		order.setDelivery(delivery);
		order.setCustomerEmailAddress("leo@hooghly.com");
		order.setDelivery(delivery);
		order.setIpAddress("ipAddress" );
		order.setMerchant(store);
		order.setOrderDateFinished(new Date());//committed date
		
		orderStatusHistory.setComments("We received your order");
		orderStatusHistory.setCustomerNotified(1);
		orderStatusHistory.setStatus(OrderStatus.ORDERED);
		orderStatusHistory.setDateAdded(new Date() );
		orderStatusHistory.setOrder(order);
		order.getOrderHistory().add( orderStatusHistory );		
		

		order.setPaymentType(PaymentType.PAYPAL);
		order.setPaymentModuleCode("paypal");
		order.setStatus( OrderStatus.DELIVERED);
		order.setTotal(new BigDecimal(23.99));
		
		
		//OrderProductDownload - Digital download
		OrderProductDownload orderProductDownload = new OrderProductDownload();
		orderProductDownload.setDownloadCount(1);
		orderProductDownload.setMaxdays(31);		
		orderProductDownload.setOrderProductFilename("Your digital file name");
		
		//OrderProductPrice
		OrderProductPrice oproductprice = new OrderProductPrice();
		oproductprice.setDefaultPrice(true);	
		oproductprice.setProductPrice(new BigDecimal(19.99) );
		oproductprice.setProductPriceCode("baseprice" );
		oproductprice.setProductPriceName("Base Price" );
		//oproductprice.setProductPriceSpecialAmount(new BigDecimal(13.99) );	

		
		//OrderProduct
		OrderProduct oproduct = new OrderProduct();
		oproduct.getDownloads().add( orderProductDownload);
		oproduct.setOneTimeCharge( new BigDecimal(19.99) );
		oproduct.setOrder(order);		
		oproduct.setProductName( "Product name" );
		oproduct.setProductQuantity(1);
		oproduct.setSku("TB12345" );		
		oproduct.getPrices().add(oproductprice ) ;
		
		oproductprice.setOrderProduct(oproduct);		
		orderProductDownload.setOrderProduct(oproduct);
		order.getOrderProducts().add(oproduct);

		//OrderTotal
		OrderTotal subtotal = new OrderTotal();	
		subtotal.setModule("summary" );		
		subtotal.setSortOrder(0);
		subtotal.setText("Summary" );
		subtotal.setTitle("Summary" );
		subtotal.setOrderTotalCode("subtotal");
		subtotal.setValue(new BigDecimal(19.99 ) );
		subtotal.setOrder(order);
		
		order.getOrderTotal().add(subtotal);
		
		OrderTotal tax = new OrderTotal();	
		tax.setModule("tax" );		
		tax.setSortOrder(1);
		tax.setText("Tax" );
		tax.setTitle("Tax" );
		tax.setOrderTotalCode("tax");
		tax.setValue(new BigDecimal(4) );
		tax.setOrder(order);
		
		order.getOrderTotal().add(tax);
		
		OrderTotal total = new OrderTotal();	
		total.setModule("total" );		
		total.setSortOrder(2);
		total.setText("Total" );
		total.setTitle("Total" );
		total.setOrderTotalCode("total");
		total.setValue(new BigDecimal(23.99) );
		total.setOrder(order);
		
		order.getOrderTotal().add(total);
		
		orderService.create(order);	
	}

}
