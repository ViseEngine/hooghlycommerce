package co.hooghly.commerce.web.interceptor;

import static co.hooghly.commerce.constants.Constants.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import org.springframework.web.servlet.ModelAndView;
import co.hooghly.commerce.business.MerchantStoreService;

import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.MerchantStore;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(1)
public class MerchantStoreProcessingStrategy implements WebInterceptorProcessingStrategy{
	
	private static final String STORE_REQUEST_PARAMETER = "store";

	
	@Autowired
	private MerchantStoreService merchantService;
	
	
	@Override
	public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		log.info("Processing merchant store and view");
		/** merchant store **/
		MerchantStore store = (MerchantStore)request.getSession().getAttribute(Constants.MERCHANT_STORE);

		String storeCode = request.getParameter(STORE_REQUEST_PARAMETER);
		
		//remove link set from controllers for declaring active - inactive links
		request.removeAttribute(Constants.LINK_CODE);
		
		if(!StringUtils.isBlank(storeCode)) {
			if(store!=null) {
				if(!store.getCode().equals(storeCode)) {
					store = setMerchantStoreInSession(request, storeCode);
				}
			}else{ // when url sm-shop/shop is being loaded for first time store is null
				store = setMerchantStoreInSession(request, storeCode);
			}
		}

		if(store==null) {
			store = setMerchantStoreInSession(request, MerchantStore.DEFAULT_STORE);
		}
		
		request.setAttribute(Constants.MERCHANT_STORE, store);
		
	}
	
	/**
	    * Sets a MerchantStore with the given storeCode in the session.
	    * @param request
	    * @param storeCode The storeCode of the Merchant.
	    * @return the MerchantStore inserted in the session.
	    * @throws Exception
	    */
	   private MerchantStore setMerchantStoreInSession(HttpServletRequest request, String storeCode) {
		   if(storeCode == null || request == null)
			   return null;
		   MerchantStore store = merchantService.getByCode(storeCode);
			if(store!=null) {
				request.getSession().setAttribute(Constants.MERCHANT_STORE, store);
			}		
			return store;
	   }

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		
		
	}

	@Override
	public boolean canHandle(String clazz) {
		return StringUtils.equals(clazz, "StoreInterceptor");
		
	}
	
	
}
