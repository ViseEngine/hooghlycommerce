package co.hooghly.commerce.api;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.CustomerOptionService;
import co.hooghly.commerce.business.CustomerOptionValueService;
import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.business.EmailService;
import co.hooghly.commerce.business.GroupService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.admin.UserReset;
import co.hooghly.commerce.util.EmailTemplatesUtils;
import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.util.LocaleUtils;
import co.hooghly.commerce.web.populator.CustomerPopulator;
import co.hooghly.commerce.web.populator.PersistableCustomerOptionPopulator;
import co.hooghly.commerce.web.populator.PersistableCustomerOptionValuePopulator;
import co.hooghly.commerce.web.populator.ReadableCustomerPopulator;
import co.hooghly.commerce.web.ui.PersistableCustomer;
import co.hooghly.commerce.web.ui.PersistableCustomerOption;
import co.hooghly.commerce.web.ui.PersistableCustomerOptionValue;
import co.hooghly.commerce.web.ui.ReadableCustomer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/api")
public class CustomerController {

	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private CustomerOptionValueService customerOptionValueService;
	
	@Autowired
	private CustomerOptionService customerOptionService;
	
	
	@Autowired
	private MerchantStoreService merchantStoreService;
	
	@Autowired
	private LanguageService languageService;
	

	@Autowired
	private CountryService countryService;
	
	@Autowired
	private GroupService   groupService;
	
	@Autowired
	private ZoneService zoneService;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	private LabelUtils messages;
	
	@Autowired
	private EmailTemplatesUtils emailTemplatesUtils;


	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCategoryRESTController.class);
	
	
	/**
	 * Returns a single customer for a given MerchantStore
	 */
	@RequestMapping( value="/{store}/customer/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ReadableCustomer getCustomer(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		if(merchantStore!=null) {
			if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		Customer customer = customerService.getById(id);
		co.hooghly.commerce.web.ui.Customer customerProxy;
		if(customer == null){
			response.sendError(404, "No Customer found with id : " + id);
			return null;
		}
		
		ReadableCustomerPopulator populator = new ReadableCustomerPopulator();
		ReadableCustomer readableCustomer = new ReadableCustomer();
		populator.populate(customer, readableCustomer, merchantStore, merchantStore.getDefaultLanguage());
		
		return readableCustomer;
	}
	
	
	/**
	 * Create a customer option value
	 * @param store
	 * @param optionValue
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping( value="/private/{store}/customer/optionValue", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableCustomerOptionValue createCustomerOptionValue(@PathVariable final String store, @Valid @RequestBody PersistableCustomerOptionValue optionValue, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			if(merchantStore!=null) {
				if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}

			PersistableCustomerOptionValuePopulator populator = new PersistableCustomerOptionValuePopulator();
			populator.setLanguageService(languageService);
			
			co.hooghly.commerce.domain.CustomerOptionValue optValue = new co.hooghly.commerce.domain.CustomerOptionValue();
			populator.populate(optionValue, optValue, merchantStore, merchantStore.getDefaultLanguage());
		
			customerOptionValueService.save(optValue);
			
			optionValue.setId(optValue.getId());
			
			return optionValue;
			
		} catch (Exception e) {
			LOGGER.error("Error while saving customer option value",e);
			try {
				response.sendError(503, "Error while saving product option value" + e.getMessage());
			} catch (Exception ignore) {
			}	
			return null;
		}
	}
	
	/**
	 * Create a customer option
	 * @param store
	 * @param option
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping( value="/private/{store}/customer/option", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableCustomerOption createCustomerOption(@PathVariable final String store, @Valid @RequestBody PersistableCustomerOption option, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		try {
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			if(merchantStore!=null) {
				if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}

			PersistableCustomerOptionPopulator populator = new PersistableCustomerOptionPopulator();
			populator.setLanguageService(languageService);
			
			co.hooghly.commerce.domain.CustomerOption opt = new co.hooghly.commerce.domain.CustomerOption();
			populator.populate(option, opt, merchantStore, merchantStore.getDefaultLanguage());
		
			customerOptionService.save(opt);
			
			option.setId(opt.getId());
			
			return option;
			
		} catch (Exception e) {
			LOGGER.error("Error while saving customer option",e);
			try {
				response.sendError(503, "Error while saving product option value" + e.getMessage());
			} catch (Exception ignore) {
			}	
			return null;
		}
	}
	
	
	/**
	 * Returns all customers for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/customer", method=RequestMethod.GET)
	@ResponseBody
	public List<ReadableCustomer> getCustomers(@PathVariable final String store, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		if(merchantStore!=null) {
			if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		List<Customer> customers = customerService.listByStore(merchantStore);
		List<ReadableCustomer> returnCustomers = new ArrayList<ReadableCustomer>();
		for(Customer customer : customers) {

			ReadableCustomerPopulator populator = new ReadableCustomerPopulator();
			ReadableCustomer readableCustomer = new ReadableCustomer();
			populator.populate(customer, readableCustomer, merchantStore, merchantStore.getDefaultLanguage());
			returnCustomers.add(readableCustomer);
			
		}
		
		return returnCustomers;
	}

	
	
	/**
	 * Deletes a customer for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/customer/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCustomer(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		
		try {
			
			Customer customer = customerService.getById(id);
			
			if(customer==null) {
				response.sendError(404, "No Customer found for ID : " + id);
				return;
			} 
				
				MerchantStore merchantStore = merchantStoreService.getByCode(store);
				if(merchantStore == null) {
					response.sendError(404, "Invalid merchant store : " + store);
					return;
				}
				
				if(merchantStore.getId().intValue()!= customer.getMerchantStore().getId().intValue()){
					response.sendError(404, "Customer id: " + id + " is not part of store " + store);
					return;
				}			
				
				customerService.delete(customer);
			
			
		} catch (ServiceException se) {
			LOGGER.error("Cannot delete customer",se);
			response.sendError(404, "An exception occured while removing the customer");
			return;
		}

	}
	
	
	/**
	 * Create new customer for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/customer", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableCustomer createCustomer(@PathVariable final String store, @Valid @RequestBody PersistableCustomer customer, HttpServletRequest request, HttpServletResponse response) throws Exception {
		MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		if(merchantStore!=null) {
			if(!merchantStore.getCode().equals(store)) {
				merchantStore = null;
			}
		}
		
		if(merchantStore== null) {
			merchantStore = merchantStoreService.getByCode(store);
		}
		
		if(merchantStore==null) {
			LOGGER.error("Merchant store is null for code " + store);
			response.sendError(503, "Merchant store is null for code " + store);
			return null;
		}
		
		Customer cust = new Customer();
		
		CustomerPopulator populator = new CustomerPopulator();
		populator.setCountryService(countryService);
		populator.setCustomerOptionService(customerOptionService);
		populator.setCustomerOptionValueService(customerOptionValueService);
		populator.setLanguageService(languageService);
		populator.setLanguageService(languageService);
		populator.setZoneService(zoneService);
		populator.populate(customer, cust, merchantStore, merchantStore.getDefaultLanguage());
		
		List<Group> groups = groupService.listGroup(GroupType.ADMIN);
		cust.setGroups(groups);

		Locale customerLocale = LocaleUtils.getLocale(cust.getDefaultLanguage());
		
		String password = customer.getClearPassword();
		if(StringUtils.isBlank(password)) {
			password = UserReset.generateRandomString();
			customer.setClearPassword(password);
		}

		@SuppressWarnings("deprecation")
		String encodedPassword = passwordEncoder.encode(password);
		if(!StringUtils.isBlank(customer.getEncodedPassword())) {
			encodedPassword = customer.getEncodedPassword();
			customer.setClearPassword("");
		}
		
		customer.setEncodedPassword(encodedPassword);
		customerService.save(cust);
		customer.setId(cust.getId());
		
		emailTemplatesUtils.sendRegistrationEmail(customer, merchantStore, customerLocale, request.getContextPath());


		return customer;
	}
	
}
