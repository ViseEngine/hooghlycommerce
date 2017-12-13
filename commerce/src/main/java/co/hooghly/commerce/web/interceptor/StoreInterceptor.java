package co.hooghly.commerce.web.interceptor;

import co.hooghly.commerce.business.MerchantConfigurationService;
import co.hooghly.commerce.business.utils.CoreConfiguration;
import static co.hooghly.commerce.constants.Constants.*;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Spring MVC interceptor.
 * 
 * @author Dhrubo
 */
@Slf4j
public class StoreInterceptor extends HandlerInterceptorAdapter {

	
	@Autowired
	private List<WebInterceptorProcessingStrategy> processingStrategies;
	

	@Autowired
	private MerchantConfigurationService merchantConfigurationService;

	//@Autowired
	//private LabelUtils messages;

	//@Autowired
	//private CategoryFacade categoryFacade;

	@Autowired
	private CoreConfiguration coreConfiguration;
	
	

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		log.info("Post handling request for store.");
		for(WebInterceptorProcessingStrategy strategy : processingStrategies) {
			if(strategy.canHandle("StoreInterceptor")) {
				strategy.postHandle(request, response, handler,modelAndView);
			}
		}
	}



	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		log.info("Pre handling request for store.");

		for(WebInterceptorProcessingStrategy strategy : processingStrategies) {
			if(strategy.canHandle("StoreInterceptor")) {
				strategy.preHandle(request, response, handler);
			}
		}

		

		
		/******* Configuration objects *******/

		/**
		 * SHOP configuration type Should contain - Different configuration
		 * flags - Google analytics - Facebook page - Twitter handle - Show
		 * customer login - ...
		 */

		// this.getMerchantConfigurations(store, request);

		/******* Shopping Cart *********/

		String shoppingCartCode = (String) request.getSession().getAttribute(SHOPPING_CART);
		if (StringUtils.isNotEmpty(shoppingCartCode)) {
			request.setAttribute(REQUEST_SHOPPING_CART, shoppingCartCode);
		}

		return true;

	}

	
	/*
	@SuppressWarnings("unchecked")
	private void getMerchantConfigurations(MerchantStore store, HttpServletRequest request) throws Exception {

		StringBuilder configKey = new StringBuilder();
		configKey.append(store.getId()).append("_").append(CONFIG_CACHE_KEY);

		StringBuilder configKeyMissed = new StringBuilder();
		configKeyMissed.append(configKey.toString()).append(MISSED_CACHE_KEY);

		Map<String, Object> configs = null;

		if (store.isUseCache()) {

			// get from the cache
			configs = (Map<String, Object>) cache.getFromCache(configKey.toString());
			if (configs == null) {
				// get from missed cache
				// Boolean missedContent =
				// (Boolean)cache.getFromCache(configKeyMissed.toString());

				// if( missedContent==null) {
				configs = this.getConfigurations(store);
				// put in cache

				if (configs != null) {
					cache.putInCache(configs, configKey.toString());
				} else {
					// put in missed cache
					// cache.putInCache(new Boolean(true),
					// configKeyMissed.toString());
				}
				// }
			}

		} else {
			configs = this.getConfigurations(store);
		}

		if (configs != null && configs.size() > 0) {
			request.setAttribute(REQUEST_CONFIGS, configs);
		}

	}

	@SuppressWarnings("unused")
	private Map<String, Object> getConfigurations(MerchantStore store) {

		Map<String, Object> configs = new HashMap<String, Object>();
		try {

			List<MerchantConfiguration> merchantConfiguration = merchantConfigurationService
					.listByType(MerchantConfigurationType.CONFIG, store);

			if (CollectionUtils.isEmpty(merchantConfiguration)) {
				return configs;
			}

			for (MerchantConfiguration configuration : merchantConfiguration) {
				configs.put(configuration.getKey(), configuration.getValue());
			}

			configs.put(SHOP_SCHEME, coreConfiguration.getProperty(SHOP_SCHEME));
			configs.put(FACEBOOK_APP_ID, coreConfiguration.getProperty(FACEBOOK_APP_ID));

			// get MerchantConfig
			MerchantConfig merchantConfig = merchantConfigurationService.getMerchantConfig(store);
			if (merchantConfig != null) {
				if (configs == null) {
					configs = new HashMap<String, Object>();
				}

				ObjectMapper m = new ObjectMapper();
				@SuppressWarnings("unchecked")
				Map<String, Object> props = m.convertValue(merchantConfig, Map.class);

				for (String key : props.keySet()) {
					configs.put(key, props.get(key));
				}
			}
		} catch (Exception e) {
			log.error("Exception while getting configurations", e);
		}

		return configs;

	}

	*/
}
