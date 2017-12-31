package co.hooghly.commerce.web.interceptor;

import static co.hooghly.commerce.constants.Constants.*;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import co.hooghly.commerce.domain.MerchantStoreView;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(2)
public class LanguageProcessingStrategy implements WebInterceptorProcessingStrategy{

	@Override
	public boolean canHandle(String clazz) {
		return StringUtils.equals(clazz, "StoreInterceptor");
		
	}

	@Override
	public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		log.info("Processing language");
		MerchantStoreView merchantStoreView = (MerchantStoreView) request.getAttribute(MERCHANT_STORE_VIEW);
		findAndStoreLanguageWithLocale(request,response,merchantStoreView);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Use the locale from the Merchant store view not browser, this will
	 * simplify things.
	 * 
	 * @param request
	 * @param response
	 * @param store
	 */

	private Locale findAndStoreLanguageWithLocale(HttpServletRequest request, HttpServletResponse response,
			MerchantStoreView storeView) {
		Locale locale = storeView.computeLocale();
		log.info("Computed locale from store view - {}", locale);

		LocaleContextHolder.setLocale(locale);
		WebUtils.setSessionAttribute(request, LANGUAGE, storeView.getLanguage());
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver != null) {
			localeResolver.setLocale(request, response, locale);
		}
		response.setLocale(locale);
		request.setAttribute(LANGUAGE, storeView.getLanguage());

		return locale;

	}


}
