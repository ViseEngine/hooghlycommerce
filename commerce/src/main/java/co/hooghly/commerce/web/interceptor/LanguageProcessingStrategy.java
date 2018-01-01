package co.hooghly.commerce.web.interceptor;

import static co.hooghly.commerce.constants.Constants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.web.ui.Breadcrumb;
import co.hooghly.commerce.web.ui.BreadcrumbItem;
import co.hooghly.commerce.web.ui.BreadcrumbItemType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(2)
public class LanguageProcessingStrategy implements WebInterceptorProcessingStrategy {
	
	@Autowired
	private LanguageService languageService;

	@Autowired
	private MessageSource messageSource;
		
	@Autowired
	private ProductService productService;
	
	private CategoryService categoryService;
	
	@Override
	public boolean canHandle(String clazz) {
		return StringUtils.equals(clazz, "StoreInterceptor");
	}

	@Override
	public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		log.info("Processing language");

		Language language = getRequestLanguage(request, response);
		request.setAttribute(Constants.LANGUAGE, language);

		Locale locale = languageService.toLocale(language);
		LocaleContextHolder.setLocale(locale);

		/** Breadcrumbs **/
		setBreadcrumb(request, locale);

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) {

	}

	/**
	 * Determines request language based on store rules
	 * 
	 * @param request
	 * @return
	 */
	public Language getRequestLanguage(HttpServletRequest request, HttpServletResponse response) {

		Language language = (Language) WebUtils.getSessionAttribute(request, LANGUAGE);
		// should be browser locale
		Locale locale = LocaleContextHolder.getLocale();

		log.info("Determining store view  locale - {}", locale);

		if (language == null) {
			try {

				MerchantStore store = (MerchantStore) WebUtils.getSessionAttribute(request, MERCHANT_STORE);
				language = store.getDefaultLanguage(); // language is mandatory
														// for store so if check
														// redundant

				locale = languageService.toLocale(language);
				if (locale != null) {
					LocaleContextHolder.setLocale(locale);
				}
				WebUtils.setSessionAttribute(request, LANGUAGE, language);

				if (language == null) {
					language = languageService.toLanguage(locale);
					WebUtils.setSessionAttribute(request, LANGUAGE, language);
				}

			} catch (Exception e) {
				if (language == null) {
					try {
						language = languageService.getByCode(DEFAULT_LANGUAGE);
					} catch (Exception ignore) {
					}
				}
			}
		} else {

			if (!language.getCode().equals(locale.getLanguage())) {
				// get locale context
				language = languageService.toLanguage(locale);
			}

		}

		if (language != null) {
			locale = languageService.toLocale(language);
		} else {
			language = languageService.toLanguage(locale);
		}

		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver != null) {
			localeResolver.setLocale(request, response, locale);
		}
		response.setLocale(locale);

		return language;
	}

	private void setBreadcrumb(HttpServletRequest request, Locale locale) {

		try {

			// breadcrumb
			Breadcrumb breadCrumb = (Breadcrumb) request.getSession().getAttribute(Constants.BREADCRUMB);
			Language language = (Language) request.getAttribute(Constants.LANGUAGE);
			if (breadCrumb == null) {
				breadCrumb = new Breadcrumb();
				breadCrumb.setLanguage(language);
				BreadcrumbItem item = getDefaultBreadcrumbItem(language, locale);
				breadCrumb.getBreadCrumbs().add(item);
			} else {

				// check language
				if (language.getCode().equals(breadCrumb.getLanguage().getCode())) {

					// rebuild using the appropriate language
					List<BreadcrumbItem> items = new ArrayList<>();
					for (BreadcrumbItem item : breadCrumb.getBreadCrumbs()) {

						if (item.getItemType().name().equals(BreadcrumbItemType.HOME)) {
							BreadcrumbItem homeItem = this.getDefaultBreadcrumbItem(language, locale);
							homeItem.setItemType(BreadcrumbItemType.HOME);
							homeItem.setLabel(messageSource.getMessage(Constants.HOME_MENU_KEY, null, locale));
							homeItem.setUrl(Constants.HOME_URL);
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
								//categoryItem.setLabel(category.getDescription().getName());
								//categoryItem.setUrl(category.getDescription().getSeUrl());
								items.add(categoryItem);
							}
						} /*else if (item.getItemType().name().equals(BreadcrumbItemType.PAGE)) {
							Content content = contentService.getByLanguage(item.getId(), language);
							if (content != null) {
								BreadcrumbItem contentItem = new BreadcrumbItem();
								contentItem.setId(content.getId());
								contentItem.setItemType(BreadcrumbItemType.PAGE);
								contentItem.setLabel(content.getDescription().getName());
								contentItem.setUrl(content.getDescription().getSeUrl());
								items.add(contentItem);
							}
						}*/

					}

					breadCrumb = new Breadcrumb();
					breadCrumb.setLanguage(language);
					breadCrumb.setBreadCrumbs(items);

				}

			}

			request.getSession().setAttribute(Constants.BREADCRUMB, breadCrumb);
			request.setAttribute(Constants.BREADCRUMB, breadCrumb);

		} catch (Exception e) {
			log.error("Error while building breadcrumbs", e);
		}

	}

	private BreadcrumbItem getDefaultBreadcrumbItem(Language language, Locale locale) {

		// set home page item
		BreadcrumbItem item = new BreadcrumbItem();
		item.setItemType(BreadcrumbItemType.HOME);
		item.setLabel(messageSource.getMessage(Constants.HOME_MENU_KEY,null, locale));
		item.setUrl(Constants.HOME_URL);
		return item;

	}
}
