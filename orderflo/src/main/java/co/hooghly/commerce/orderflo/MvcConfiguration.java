package co.hooghly.commerce.orderflo;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


import co.hooghly.commerce.orderflo.controller.interceptor.UiMetaDataPopulator;


@Configuration
public class MvcConfiguration extends WebMvcConfigurerAdapter {
	
	
	@Bean
	public UiMetaDataPopulator uiMetaDataLoadingInterceptor(){
		return BeanUtils.instantiate(UiMetaDataPopulator.class);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(uiMetaDataLoadingInterceptor()).excludePathPatterns("/webjars/**");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:user/login");
		
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	//	argumentResolvers.add(new DatatablesRequestMethodArgumentResolver());
	}
}