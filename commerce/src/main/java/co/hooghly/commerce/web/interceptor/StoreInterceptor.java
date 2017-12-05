package co.hooghly.commerce.web.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.CustomerService;

import co.hooghly.commerce.business.MerchantConfigurationService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.MerchantStoreViewService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.business.utils.CacheUtils;
import co.hooghly.commerce.business.utils.CoreConfiguration;
import static co.hooghly.commerce.constants.Constants.*;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.CategoryDescription;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantConfig;
import co.hooghly.commerce.domain.MerchantConfiguration;
import co.hooghly.commerce.domain.MerchantConfigurationType;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.MerchantStoreView;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.facade.CategoryFacade;
import co.hooghly.commerce.util.GeoLocationUtils;
import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.util.LanguageUtils;
import co.hooghly.commerce.web.populator.ReadableCategoryPopulator;
import co.hooghly.commerce.domain.Address;
import co.hooghly.commerce.domain.Billing;
import co.hooghly.commerce.web.ui.AnonymousCustomer;
import co.hooghly.commerce.web.ui.Breadcrumb;
import co.hooghly.commerce.web.ui.BreadcrumbItem;
import co.hooghly.commerce.web.ui.BreadcrumbItemType;
import co.hooghly.commerce.web.ui.PageInformation;
import co.hooghly.commerce.web.ui.ReadableCategory;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

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

	private static final String STORE_VIEW_REQUEST_PARAMETER = "storeView";

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductService productService;

	@Autowired
	private MerchantStoreService merchantService;

	@Autowired
	private CustomerService customerService;

	@Autowired
	private MerchantConfigurationService merchantConfigurationService;

	@Autowired
	private LabelUtils messages;

	@Autowired
	private CacheUtils cache;

	@Autowired
	private CategoryFacade categoryFacade;

	@Autowired
	private CoreConfiguration coreConfiguration;

	@Autowired
	private MerchantStoreViewService merchantStoreViewService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		log.info("Pre handling request for store.");

		/** merchant store view **/
		MerchantStoreView storeView = findAndSetMerchantStoreView(request);

		/** customer **/
		// findCustomer(request, storeView.getMerchantStore());

		/** anonymous customer **/
		// findAndSetAnonymousCustomer(request,
		// storeView.getMerchantStore());

		/** language & locale **/
		// Locale locale = findAndStoreLanguageWithLocale(request,
		// response,storeView.getMerchantStore());

		/** Breadcrumbs **/
		// TODO move to CMS page and controller
		// setBreadcrumb(request, locale);

		/******* Top Categories ********/
		// this.getTopCategories(store, language, request);
		// this.setTopCategories(store, language, request);

		/******* Default metatags *******/

		/**
		 * Title Description Keywords
		 */

		// all these will come from CMS view definition

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

	protected MerchantStoreView findAndSetMerchantStoreView(HttpServletRequest request) throws Exception {
		/** merchant store **/
		MerchantStoreView storeView = (MerchantStoreView) WebUtils.getSessionAttribute(request, MERCHANT_STORE_VIEW);
		String storeViewCode = ServletRequestUtils.getStringParameter(request, STORE_VIEW_REQUEST_PARAMETER);

		if (StringUtils.isNotBlank(storeViewCode) && storeView != null) {
			// A store code found in request and session so handle the
			// conflict by
			// trying to use the request param store code.
			// override the session store code with request store code.
			// the user might have requested a change by selecting language or
			// currency
			storeView = setMerchantStoreViewInSession(request, storeViewCode);
		}

		if (storeView == null) {
			// merchant store not found in session or override did not work, set
			// default - this is for first time use
			storeView = setMerchantStoreViewInSession(request, null);
			// set default store view .

		}
		request.setAttribute(MERCHANT_STORE, storeView.getMerchantStore());
		request.setAttribute(MERCHANT_STORE_VIEW, storeView);

		return storeView;
	}

	private MerchantStoreView setMerchantStoreViewInSession(HttpServletRequest request, String storeViewCode) {
		MerchantStoreView view = null;
		MerchantStore store = null;
		Optional<MerchantStoreView> mView = Optional.empty();
		if (StringUtils.isNotBlank(storeViewCode)) {
			mView = merchantStoreViewService.findByCode(storeViewCode);

		} else {
			// use default merchant store , first time request
			store = merchantService.getByCode(MerchantStore.DEFAULT_STORE);
			mView = store.getStoreViews().stream().filter(i -> i.isDefaultView()).findFirst();
		}

		if (mView.isPresent()) {
			view = mView.get();
			request.getSession().setAttribute(MERCHANT_STORE, view.getMerchantStore());
			request.getSession().setAttribute(MERCHANT_STORE_VIEW, view);
		}

		return view;
	}

	/**
	 * Rules ========== first time ========== 1. Get browser locale 2. check if
	 * language exists for the merchant 3. if not use default langauge
	 * ====================== user selects a language ====================== 1.
	 * check if the language exists for the merchant 2. if not use default
	 * langauge
	 * 
	 * @param request
	 * @param response
	 * @param store
	 */
	/*
	private Locale findAndStoreLanguageWithLocale(HttpServletRequest request, HttpServletResponse response,
			MerchantStore store) {
		Locale locale = LocaleContextHolder.getLocale(); // browser locale.

		Optional<Language> language = store.getLanguages().stream()
				.filter(lang -> StringUtils.equals(locale.getLanguage(), lang.getCode())).findFirst();
		Language lang = language.isPresent() ? language.get() : store.getDefaultLanguage();

		LocaleContextHolder.setLocale(locale);
		WebUtils.setSessionAttribute(request, LANGUAGE, lang);
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver != null) {
			localeResolver.setLocale(request, response, locale);
		}
		response.setLocale(locale);
		request.setAttribute(LANGUAGE, lang);

		return locale;

	}*/

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

	private Customer findCustomer(HttpServletRequest request, MerchantStore store) {
		Customer customer = (Customer) WebUtils.getSessionAttribute(request, CUSTOMER);
		if (customer != null) {
			if (customer.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				request.getSession().removeAttribute(CUSTOMER);
			}

			request.setAttribute(CUSTOMER, customer);
		} else {

			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (auth != null) {
				customer = customerService.getByNick(auth.getName());
				if (customer != null) {
					request.setAttribute(CUSTOMER, customer);
				}
			}

		}

		return customer;
	}

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

	private void setBreadcrumb(HttpServletRequest request, Locale locale) {

		try {

			// breadcrumb
			Breadcrumb breadCrumb = (Breadcrumb) request.getSession().getAttribute(BREADCRUMB);
			Language language = (Language) request.getAttribute(LANGUAGE);
			if (breadCrumb == null) {
				breadCrumb = new Breadcrumb();
				breadCrumb.setLanguage(language);
				BreadcrumbItem item = this.getDefaultBreadcrumbItem(language, locale);
				breadCrumb.getBreadCrumbs().add(item);
			} else {

				// check language
				if (language.getCode().equals(breadCrumb.getLanguage().getCode())) {

					// rebuild using the appropriate language
					List<BreadcrumbItem> items = new ArrayList<BreadcrumbItem>();
					for (BreadcrumbItem item : breadCrumb.getBreadCrumbs()) {

						if (item.getItemType().name().equals(BreadcrumbItemType.HOME)) {
							BreadcrumbItem homeItem = this.getDefaultBreadcrumbItem(language, locale);
							homeItem.setItemType(BreadcrumbItemType.HOME);
							homeItem.setLabel(messages.getMessage(HOME_MENU_KEY, locale));
							homeItem.setUrl(HOME_URL);
							items.add(homeItem);
						} else if (item.getItemType().name().equals(BreadcrumbItemType.PRODUCT)) {
							Product product = productService.getProductForLocale(item.getId(), language, locale);
							if (product != null) {
								BreadcrumbItem productItem = new BreadcrumbItem();
								productItem.setId(product.getId());
								productItem.setItemType(BreadcrumbItemType.PRODUCT);
								productItem.setLabel(product.getProductDescription().getName());
								productItem.setUrl(product.getProductDescription().getSeUrl());
								items.add(productItem);
							}
						} else if (item.getItemType().name().equals(BreadcrumbItemType.CATEGORY)) {
							Category category = categoryService.getByLanguage(item.getId(), language);
							if (category != null) {
								BreadcrumbItem categoryItem = new BreadcrumbItem();
								categoryItem.setId(category.getId());
								categoryItem.setItemType(BreadcrumbItemType.CATEGORY);
								categoryItem.setLabel(category.getDescription().getName());
								categoryItem.setUrl(category.getDescription().getSeUrl());
								items.add(categoryItem);
							}
						} else if (item.getItemType().name().equals(BreadcrumbItemType.PAGE)) {
							/*
							 * Content content =
							 * contentService.getByLanguage(item.getId(),
							 * language); if(content!=null) { BreadcrumbItem
							 * contentItem = new BreadcrumbItem();
							 * contentItem.setId(content.getId());
							 * contentItem.setItemType(BreadcrumbItemType.PAGE);
							 * contentItem.setLabel(content.getDescription().
							 * getName());
							 * contentItem.setUrl(content.getDescription().
							 * getSeUrl()); items.add(contentItem); }
							 */
						}

					}

					breadCrumb = new Breadcrumb();
					breadCrumb.setLanguage(language);
					breadCrumb.setBreadCrumbs(items);

				}

			}

			request.getSession().setAttribute(BREADCRUMB, breadCrumb);
			request.setAttribute(BREADCRUMB, breadCrumb);

		} catch (Exception e) {
			log.error("Error while building breadcrumbs", e);
		}

	}

	private BreadcrumbItem getDefaultBreadcrumbItem(Language language, Locale locale) {

		// set home page item
		BreadcrumbItem item = new BreadcrumbItem();
		item.setItemType(BreadcrumbItemType.HOME);
		item.setLabel(messages.getMessage(HOME_MENU_KEY, locale));
		item.setUrl(HOME_URL);
		return item;

	}

}
