package co.hooghly.commerce.api;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.CustomerOptionService;
import co.hooghly.commerce.business.CustomerOptionValueService;
import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.business.EmailService;
import co.hooghly.commerce.business.GroupService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.MerchantStoreService;

import co.hooghly.commerce.business.ZoneService;

import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.CustomerOption;
import co.hooghly.commerce.domain.CustomerOptionValue;
import co.hooghly.commerce.domain.Group;
import co.hooghly.commerce.domain.GroupType;
import co.hooghly.commerce.domain.MerchantStore;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.util.List;


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
	private GroupService groupService;


	@Autowired
	EmailService emailService;

	

	/**
	 * Returns a single customer for a given MerchantStore
	 */
	@GetMapping(value = "/{store}/customers/{id}")
	@ResponseBody
	public Customer getCustomer(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) {
		MerchantStore merchantStore = merchantStoreService.getByCode(store);
		return customerService.findByMerchantStoreIdAndId(merchantStore.getId(), id);
	}

	/**
	 * Create a customer option value
	 * 
	 * @param store
	 * @param optionValue
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{store}/customers/optionValue", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public CustomerOptionValue createCustomerOptionValue(@PathVariable final String store,
			@Valid @RequestBody CustomerOptionValue optionValue) {
		MerchantStore merchantStore = merchantStoreService.getByCode(store);
		optionValue.setMerchantStore(merchantStore);

		return customerOptionValueService.save(optionValue);

	}

	/**
	 * Create a customer option
	 * 
	 * @param store
	 * @param option
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PostMapping(value = "/{store}/customer/option")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public CustomerOption createCustomerOption(@PathVariable final String store,
			@Valid @RequestBody CustomerOption option) {

		MerchantStore merchantStore = merchantStoreService.getByCode(store);
		option.setMerchantStore(merchantStore);

		return customerOptionService.save(option);

	}

	/**
	 * Returns all customers for a given MerchantStore
	 */
	@RequestMapping(value = "/{store}/customers")
	@ResponseBody
	public List<Customer> findCustomersByStore(@PathVariable final String store) {
		MerchantStore merchantStore = merchantStoreService.getByCode(store);

		return customerService.listByStore(merchantStore);

	}

	/**
	 * Deletes a customer for a given MerchantStore
	 */
	@DeleteMapping(value = "/{store}/customer/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCustomer(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request,
			HttpServletResponse response) {

		MerchantStore merchantStore = merchantStoreService.getByCode(store);

		customerService.deletedByMerchantStoreIdAndId(merchantStore.getId(), id);

	}

	/**
	 * Create new customer for a given MerchantStore
	 */
	@RequestMapping(value = "/{store}/customer", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public Customer createCustomer(@PathVariable final String store,
			@Valid @RequestBody Customer customer) {
		MerchantStore merchantStore =  merchantStoreService.getByCode(store);
		customer.setMerchantStore(merchantStore);
		

		List<Group> groups = groupService.listGroup(GroupType.ADMIN);
		customer.setGroups(groups);

		
		//TODO - Fix password encoding
		customerService.save(customer);
		
		//TODO - Raise email event
		//emailTemplatesUtils.sendRegistrationEmail(customer, merchantStore, customerLocale, request.getContextPath());

		return customer;
	}

}
