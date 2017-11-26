package co.hooghly.commerce.shop.controller;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.CustomerAttributeService;
import co.hooghly.commerce.business.CustomerOptionService;
import co.hooghly.commerce.business.CustomerOptionSetService;
import co.hooghly.commerce.business.CustomerOptionValueService;
import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.OrderService;
import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.CustomerAttribute;
import co.hooghly.commerce.domain.CustomerOptionType;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.facade.CustomerFacade;
import co.hooghly.commerce.facade.OrderFacade;
import co.hooghly.commerce.util.EmailTemplatesUtils;
import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.util.LanguageUtils;
import co.hooghly.commerce.util.LocaleUtils;
import co.hooghly.commerce.web.populator.ReadableCustomerPopulator;
import co.hooghly.commerce.web.ui.Address;
import co.hooghly.commerce.web.ui.AjaxResponse;
import co.hooghly.commerce.web.ui.CustomerEntity;
import co.hooghly.commerce.web.ui.CustomerPassword;
import co.hooghly.commerce.web.ui.ReadableCustomer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * Entry point for logged in customers
 *
 */
@Controller
@RequestMapping("/shop/customer")
@ConditionalOnProperty(prefix="shop.controller.CustomerAccountController", name="enabled")
public class CustomerAccountController extends AbstractController {
	
	private static final String CUSTOMER_ID_PARAMETER = "customer";
    private static final String BILLING_SECTION="/shop/customer/billing.html";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomerAccountController.class);
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private CustomerOptionService customerOptionService;
	
	@Inject
	private CustomerOptionValueService customerOptionValueService;
	
	@Inject
	private CustomerOptionSetService customerOptionSetService;
	
	@Inject
	private CustomerAttributeService customerAttributeService;
	
    @Inject
    private LanguageService languageService;
    
    @Inject
    private LanguageUtils languageUtils;
    
	@Inject
	private PasswordEncoder passwordEncoder;


    @Inject
    private CountryService countryService;
    
	@Inject
	private EmailTemplatesUtils emailTemplatesUtils;

    
    @Inject
    private ZoneService zoneService;
    
    @Inject
    private CustomerFacade customerFacade;
    
    @Inject
    private OrderService orderService;
    
    @Inject
    private OrderFacade orderFacade;
    
	@Inject
	private LabelUtils messages;


	
	/**
	 * Dedicated customer logon page
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/customLogon.html", method=RequestMethod.GET)
	public String displayLogon(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);


		//dispatch to dedicated customer logon
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.customerLogon).append(".").append(store.getStoreTemplate());

		return template.toString();
		
	}
	
	
	@RequestMapping(value="/accountSummary.json", method=RequestMethod.GET)
	public @ResponseBody ReadableCustomer customerInformation(@RequestParam String userName, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
	
	
		MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        } else {
        	response.sendError(401, "Customer not authenticated");
			return null;
        }
    	
    	if(StringUtils.isBlank(userName)) {
        	response.sendError(403, "Customer name required");
			return null;
    	}
    	
    	if(customer==null) {
        	response.sendError(401, "Customer not authenticated");
			return null;
    	}
    	
    	if(!customer.getNick().equals(userName)) {
        	response.sendError(401, "Customer not authenticated");
			return null;
    	}
    	
    	
    	ReadableCustomer readableCustomer = new ReadableCustomer();
    	

    	Language lang = languageUtils.getRequestLanguage(request, response);
    	
    	ReadableCustomerPopulator readableCustomerPopulator = new ReadableCustomerPopulator();
    	readableCustomerPopulator.populate(customer, readableCustomer, store, lang);
    	
    	return readableCustomer;
		
	}
		
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/account.html", method=RequestMethod.GET)
	public String displayCustomerAccount(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);

		
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.customer).append(".").append(store.getStoreTemplate());

		return template.toString();
		
	}
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/password.html", method=RequestMethod.GET)
	public String displayCustomerChangePassword(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);

		CustomerPassword customerPassword = new CustomerPassword();
		model.addAttribute("password", customerPassword);
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.changePassword).append(".").append(store.getStoreTemplate());

		return template.toString();
		
	}
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/changePassword.html", method=RequestMethod.POST)
	public String changePassword(@Valid @ModelAttribute(value="password") CustomerPassword password, BindingResult bindingResult, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
	    
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.changePassword).append(".").append(store.getStoreTemplate());

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	if(customer==null) {
    		return "redirect:/"+Constants.SHOP_URI;
    	}
    	
    	String currentPassword = password.getCurrentPassword();
    	String encodedCurrentPassword = passwordEncoder.encode(currentPassword);
    	
    	if(!StringUtils.equals(encodedCurrentPassword, customer.getPassword())) {
			FieldError error = new FieldError("password","password",messages.getMessage("message.invalidpassword", locale));
        	bindingResult.addError(error);
    	}

    	
        if ( bindingResult.hasErrors() )
        {
            LOGGER.info( "found {} validation error while validating customer password",
                         bindingResult.getErrorCount() );
    		return template.toString();

        }
    	
		CustomerPassword customerPassword = new CustomerPassword();
		model.addAttribute("password", customerPassword);
		
		String newPassword = password.getPassword();
		String encodedPassword = passwordEncoder.encode(newPassword);
		
		customer.setPassword(encodedPassword);
		
		customerService.saveOrUpdate(customer);
		
		emailTemplatesUtils.changePasswordNotificationEmail(customer, store, LocaleUtils.getLocale(customer.getDefaultLanguage()), request.getContextPath());
		
		model.addAttribute("success", "success");

		return template.toString();
		
	}
	

	
	/**
	 * Manage the edition of customer attributes
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value={"/attributes/save.html"}, method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> saveCustomerAttributes(HttpServletRequest request, Locale locale) throws Exception {
		

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
		
		//1=1&2=on&3=eeee&4=on&customer=1

		@SuppressWarnings("rawtypes")
		Enumeration parameterNames = request.getParameterNames();
		
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	if(customer==null) {
    		LOGGER.error("Customer id [customer] is not defined in the parameters");
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
    	}
		
		

		
		if(customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
			LOGGER.error("Customer id does not belong to current store");
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			String returnString = resp.toJSONString();
			return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		}
		
		List<CustomerAttribute> customerAttributes = customerAttributeService.getByCustomer(store, customer);
		Map<Long,CustomerAttribute> customerAttributesMap = new HashMap<Long,CustomerAttribute>();
		
		for(CustomerAttribute attr : customerAttributes) {
			customerAttributesMap.put(attr.getCustomerOption().getId(), attr);
		}

		parameterNames = request.getParameterNames();
		
		while(parameterNames.hasMoreElements()) {
			
			String parameterName = (String)parameterNames.nextElement();
			String parameterValue = request.getParameter(parameterName);
			try {
				
				String[] parameterKey = parameterName.split("-");
				co.hooghly.commerce.domain.CustomerOption customerOption = null;
				co.hooghly.commerce.domain.CustomerOptionValue customerOptionValue = null;

				
				if(CUSTOMER_ID_PARAMETER.equals(parameterName)) {
					continue;
				}
				
					if(parameterKey.length>1) {
						//parse key - value
						String key = parameterKey[0];
						String value = parameterKey[1];
						//should be on
						customerOption = customerOptionService.getById(new Long(key));
						customerOptionValue = customerOptionValueService.getById(new Long(value));
						

						
					} else {
						customerOption = customerOptionService.getById(new Long(parameterName));
						customerOptionValue = customerOptionValueService.getById(new Long(parameterValue));

					}
					
					//get the attribute
					//CustomerAttribute attribute = customerAttributeService.getByCustomerOptionId(store, customer.getId(), customerOption.getId());
					CustomerAttribute attribute = customerAttributesMap.get(customerOption.getId());
					if(attribute==null) {
						attribute = new CustomerAttribute();
						attribute.setCustomer(customer);
						attribute.setCustomerOption(customerOption);
					} else {
						customerAttributes.remove(attribute);
					}
					
					if(customerOption.getCustomerOptionType().equals(CustomerOptionType.Text.name())) {
						if(!StringUtils.isBlank(parameterValue)) {
							attribute.setCustomerOptionValue(customerOptionValue);
							attribute.setTextValue(parameterValue);
						}  else {
							attribute.setTextValue(null);
						}
					} else {
						attribute.setCustomerOptionValue(customerOptionValue);
					}
					
					
					if(attribute.getId()!=null && attribute.getId().longValue()>0) {
						if(attribute.getCustomerOptionValue()==null){
							customerAttributeService.delete(attribute);
						} else {
							customerAttributeService.update(attribute);
						}
					} else {
						customerAttributeService.save(attribute);
					}
					


			} catch (Exception e) {
				LOGGER.error("Cannot get parameter information " + parameterName,e);
			}
			
		}
		
		//and now the remaining to be removed
		for(CustomerAttribute attr : customerAttributes) {
			customerAttributeService.delete(attr);
		}
		
		//refresh customer
		Customer c = customerService.getById(customer.getId());
		super.setSessionAttribute(Constants.CUSTOMER, c, request);
		
		resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);
		String returnString = resp.toJSONString();
		return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
		

	}

	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/billing.html", method=RequestMethod.GET)
    public String displayCustomerBillingAddress(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        

        MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
        Language language = getSessionAttribute(Constants.LANGUAGE, request);
    
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	if(customer==null) {
    		return "redirect:/"+Constants.SHOP_URI;
    	}
        
        
        CustomerEntity customerEntity = customerFacade.getCustomerDataByUserName( customer.getNick(), store, language );
        if(customer !=null){
           model.addAttribute( "customer",  customerEntity);
        }
        
        
        /** template **/
        StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.Billing).append(".").append(store.getStoreTemplate());

        return template.toString();
        
    }
    
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
    @RequestMapping(value="/editAddress.html", method={RequestMethod.GET,RequestMethod.POST})
    public String editAddress(final Model model, final HttpServletRequest request,
                              @RequestParam(value = "billingAddress", required = false) Boolean billingAddress) throws Exception {
        MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
        
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	if(customer==null) {
    		return "redirect:/"+Constants.SHOP_URI;
    	}
        
        
        
        Address address=customerFacade.getAddress( customer.getId(), store, billingAddress );
        model.addAttribute( "address", address);
        model.addAttribute( "customerId", customer.getId() );
        StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.EditAddress).append(".").append(store.getStoreTemplate());
        return template.toString();
    }
    
    
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
    @RequestMapping(value="/updateAddress.html", method={RequestMethod.GET,RequestMethod.POST})
    public String updateCustomerAddress(@Valid
                                        @ModelAttribute("address") Address address,BindingResult bindingResult,final Model model, final HttpServletRequest request,
                              @RequestParam(value = "billingAddress", required = false) Boolean billingAddress) throws Exception {
       
        MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
        
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer customer = null;
    	if(auth != null &&
        		 request.isUserInRole("AUTH_CUSTOMER")) {
    		customer = customerFacade.getCustomerByUserName(auth.getName(), store);

        }
    	
    	StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.EditAddress).append(".").append(store.getStoreTemplate());
    	
    	if(customer==null) {
    		return "redirect:/"+Constants.SHOP_URI;
    	}
    	
    	model.addAttribute( "address", address);
        model.addAttribute( "customerId", customer.getId() );
        
        
        if(bindingResult.hasErrors()){
            LOGGER.info( "found {} error(s) while validating  customer address ",
                         bindingResult.getErrorCount() );
            return template.toString();
        }
        

        Language language = getSessionAttribute(Constants.LANGUAGE, request);
        customerFacade.updateAddress( customer.getId(), store, address, language);
        
        Customer c = customerService.getById(customer.getId());
		super.setSessionAttribute(Constants.CUSTOMER, c, request);
        
        model.addAttribute("success", "success");
        
        return template.toString();

    }
    
    
	@ModelAttribute("countries")
	protected List<Country> getCountries(final HttpServletRequest request){
	    
        Language language = (Language) request.getAttribute( "LANGUAGE" );
        try
        {
            if ( language == null )
            {
                language = (Language) request.getAttribute( "LANGUAGE" );
            }

            if ( language == null )
            {
                language = languageService.getByCode( Constants.DEFAULT_LANGUAGE );
            }
            
            List<Country> countryList=countryService.getCountries( language );
            return countryList;
        }
        catch ( ServiceException e )
        {
            LOGGER.error( "Error while fetching country list ", e );

        }
        return Collections.emptyList();
    }

    //@ModelAttribute("zones")
    //public List<Zone> getZones(final HttpServletRequest request){
    //    return zoneService.list();
    //}
 




}
