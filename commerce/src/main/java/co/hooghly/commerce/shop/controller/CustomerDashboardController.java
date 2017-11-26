package co.hooghly.commerce.shop.controller;

import co.hooghly.commerce.business.CustomerOptionSetService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.CustomerAttribute;
import co.hooghly.commerce.domain.CustomerOptionSet;
import co.hooghly.commerce.domain.CustomerOptionType;
import co.hooghly.commerce.domain.CustomerOptionValueDescription;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.admin.CustomerOption;
import co.hooghly.commerce.domain.admin.CustomerOptionValue;
import co.hooghly.commerce.web.populator.ReadableCustomerOptionPopulator;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Entry point for logged in customers
 *
 */
@Controller
@RequestMapping("/shop/customer")
@ConditionalOnProperty(prefix="shop.controller.CustomerDashboardController", name="enabled")
public class CustomerDashboardController extends AbstractController {
	
	@Inject
    private AuthenticationManager customerAuthenticationManager;
	
	@Inject
	private CustomerOptionSetService customerOptionSetService;
	
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/dashboard.html", method=RequestMethod.GET)
	public String displayCustomerDashboard(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
	    Language language = (Language)request.getAttribute(Constants.LANGUAGE);
	    
		Customer customer = (Customer)request.getAttribute(Constants.CUSTOMER);
		getCustomerOptions(model, customer, store, language);
        

		model.addAttribute("section","dashboard");
		
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.customer).append(".").append(store.getStoreTemplate());

		return template.toString();
		
	}
	
	
	private void getCustomerOptions(Model model, Customer customer, MerchantStore store, Language language) throws Exception {

		Map<Long,CustomerOption> options = new HashMap<Long,CustomerOption>();
		//get options
		List<CustomerOptionSet> optionSet = customerOptionSetService.listByStore(store, language);
		if(!CollectionUtils.isEmpty(optionSet)) {
			
			
			ReadableCustomerOptionPopulator optionPopulator = new ReadableCustomerOptionPopulator();
			
			Set<CustomerAttribute> customerAttributes = customer.getAttributes();
			
			for(CustomerOptionSet optSet : optionSet) {
				
				co.hooghly.commerce.domain.CustomerOption custOption = optSet.getCustomerOption();
				if(!custOption.isActive() || !custOption.isPublicOption()) {
					continue;
				}
				CustomerOption customerOption = options.get(custOption.getId());
				
				optionPopulator.setOptionSet(optSet);
				
				
				
				if(customerOption==null) {
					customerOption = new CustomerOption();
					customerOption.setId(custOption.getId());
					customerOption.setType(custOption.getCustomerOptionType());
					customerOption.setName(custOption.getDescriptionsSettoList().get(0).getName());
					
				} 
				
				optionPopulator.populate(custOption, customerOption, store, language);
				options.put(customerOption.getId(), customerOption);

				if(!CollectionUtils.isEmpty(customerAttributes)) {
					for(CustomerAttribute customerAttribute : customerAttributes) {
						if(customerAttribute.getCustomerOption().getId().longValue()==customerOption.getId()){
							CustomerOptionValue selectedValue = new CustomerOptionValue();
							co.hooghly.commerce.domain.CustomerOptionValue attributeValue = customerAttribute.getCustomerOptionValue();
							selectedValue.setId(attributeValue.getId());
							CustomerOptionValueDescription optValue = attributeValue.getDescriptionsSettoList().get(0);
							selectedValue.setName(optValue.getName());
							customerOption.setDefaultValue(selectedValue);
							if(customerOption.getType().equalsIgnoreCase(CustomerOptionType.Text.name())) {
								selectedValue.setName(customerAttribute.getTextValue());
							} 
						}
					}
				}
			}
		}
		
		
		model.addAttribute("options", options.values());

		
	}
	
	

}
