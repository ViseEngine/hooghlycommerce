package co.hooghly.commerce.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.util.UrlPathHelper;

import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.web.argument.CustomerMethodArgumentResolver;
import co.hooghly.commerce.web.argument.MerchantStoreMethodArgumentResolver;

import co.hooghly.commerce.cms.interceptor.CmsInterceptor;
import co.hooghly.commerce.web.interceptor.StoreInterceptor;

@Configuration
public class MvcConfiguration extends WebMvcConfigurerAdapter {
	
	

	@Bean
	public StoreInterceptor storeFilter() {
		return BeanUtils.instantiate(StoreInterceptor.class);
	}

	@Bean
	public CmsInterceptor cmsInterceptor() {
		return BeanUtils.instantiate(CmsInterceptor.class);
	}



	// Changes the locale when a 'locale' request parameter is sent; e.g.
	// /?locale=de
	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		return BeanUtils.instantiate(LocaleChangeInterceptor.class);
	}

	@Bean
	public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
		return BeanUtils.instantiate(DeviceResolverHandlerInterceptor.class);
	}

	@Bean
	public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
		return BeanUtils.instantiate(DeviceHandlerMethodArgumentResolver.class);
	}

	public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {

		return BeanUtils.instantiate(MappingJackson2HttpMessageConverter.class);
	}

	@Bean
	public ByteArrayHttpMessageConverter byteArrayHttpMessageConverter() {
		ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
		arrayHttpMessageConverter.setSupportedMediaTypes(getSupportedMediaTypes());
		return arrayHttpMessageConverter;
	}

	@Bean
	public SessionLocaleResolver localeResolver() {
		SessionLocaleResolver sessionLocaleResolver = BeanUtils.instantiate(SessionLocaleResolver.class);
		sessionLocaleResolver.setDefaultLocale(Locale.ENGLISH);
		return sessionLocaleResolver;
	}



	@Bean
	public LabelUtils messages() {
		return BeanUtils.instantiate(LabelUtils.class);
	}

	

	private List<MediaType> getSupportedMediaTypes() {
		List<MediaType> list = new ArrayList<MediaType>();
		list.add(MediaType.IMAGE_JPEG);
		list.add(MediaType.IMAGE_PNG);
		list.add(MediaType.APPLICATION_OCTET_STREAM);
		return list;
	}

	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		converters.add(mappingJackson2HttpMessageConverter());
		converters.add(byteArrayHttpMessageConverter());
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
		registry.addInterceptor(deviceResolverHandlerInterceptor());
		registry.addInterceptor(storeFilter()).addPathPatterns("/shop/**", "/customer/**");
		registry.addInterceptor(cmsInterceptor()).addPathPatterns("/shop/**");
		
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("redirect:shop");
		registry.addViewController("/admin").setViewName("redirect:admin/secure/home");
	}

	/**
	 * Enables the Spring MVC @Controller programming model, matrix variables
	 * are parameters containing multiple values color=red,green,blue
	 */

	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
		UrlPathHelper urlPathHelper = new UrlPathHelper();
		urlPathHelper.setRemoveSemicolonContent(false);
		configurer.setUrlPathHelper(urlPathHelper);
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(deviceHandlerMethodArgumentResolver());
		argumentResolvers.add(BeanUtils.instantiate(MerchantStoreMethodArgumentResolver.class));
		argumentResolvers.add(BeanUtils.instantiate(CustomerMethodArgumentResolver.class));
		
	}

}