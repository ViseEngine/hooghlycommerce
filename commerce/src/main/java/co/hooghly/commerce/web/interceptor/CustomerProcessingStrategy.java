package co.hooghly.commerce.web.interceptor;

import static co.hooghly.commerce.constants.Constants.*;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Address;
import co.hooghly.commerce.domain.Billing;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.util.GeoLocationUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(3)
public class CustomerProcessingStrategy implements WebInterceptorProcessingStrategy {
	
	@Autowired
	private CustomerService customerService;

	@Override
	public boolean canHandle(String clazz) {
		return StringUtils.equals(clazz, "StoreInterceptor");

	}

	@Override
	public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		log.info("Processing customer");
		MerchantStore store = (MerchantStore) WebUtils.getSessionAttribute(request, MERCHANT_STORE);
		findAndSetAnonymousCustomer(request,  store);
		findAndSetCustomer(request, store);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		

	}

	private void findAndSetAnonymousCustomer(HttpServletRequest request, MerchantStore store) {
		Customer anonymousCustomer = (Customer) WebUtils.getSessionAttribute(request, ANONYMOUS_CUSTOMER);
		if (anonymousCustomer == null) {
			anonymousCustomer = new Customer();
			Optional<Address> geoAddress = customerService.getCustomerAddress(store,
					GeoLocationUtils.getClientIpAddress(request));
			if (!geoAddress.isPresent()) {// Copy store details
				Billing billing = new Billing();
				billing.setCountry(store.getCountry());
				billing.setZone(store.getZone());

				anonymousCustomer.setBilling(billing);
			}

			anonymousCustomer.setAnonymous(true);
			WebUtils.setSessionAttribute(request, ANONYMOUS_CUSTOMER, anonymousCustomer);
		}

		request.setAttribute(ANONYMOUS_CUSTOMER, anonymousCustomer);
	}

	private void findAndSetCustomer(HttpServletRequest request, MerchantStore store) {
		Customer customer = (Customer)request.getSession().getAttribute(Constants.CUSTOMER);
		if(customer!=null) {
			if(customer.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
				request.getSession().removeAttribute(Constants.CUSTOMER);
			}
			if(!customer.isAnonymous()) {
	        	if(!request.isUserInRole("AUTH_CUSTOMER")) {
	        			request.removeAttribute(Constants.CUSTOMER);
		        }
			}
			request.setAttribute(Constants.CUSTOMER, customer);
		} 
		
		if(customer==null) {
			
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        	if(auth != null &&
	        		 request.isUserInRole("AUTH_CUSTOMER")) {
        		customer = customerService.getByNick(auth.getName());
        		if(customer!=null) {
        			request.setAttribute(Constants.CUSTOMER, customer);
        		}
	        } 
			
		}
	}

}
