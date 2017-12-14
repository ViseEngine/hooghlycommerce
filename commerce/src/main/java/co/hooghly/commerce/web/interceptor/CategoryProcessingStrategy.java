package co.hooghly.commerce.web.interceptor;


import static co.hooghly.commerce.constants.Constants.*;
import static org.mockito.Mockito.ignoreStubs;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(5)
public class CategoryProcessingStrategy implements WebInterceptorProcessingStrategy{
	
	@Autowired
	private CategoryService categoryService;

	@Override
	public boolean canHandle(String clazz) {
		return StringUtils.equals(clazz, "StoreInterceptor");
		
	}

	@Override
	public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {
		log.info("Post handle categories");
		Language language = (Language) request.getAttribute(LANGUAGE);
		MerchantStore merchantStore = (MerchantStore) request.getAttribute(MERCHANT_STORE);
		List<Category> categories = categoryService.findByDepth(merchantStore, 0, language);
		
		categories.forEach(i -> log.info("Children - {}",i.getCategories().size() ) );
		
		modelAndView.addObject("displayCategories", categories);
	}
	
	


}
