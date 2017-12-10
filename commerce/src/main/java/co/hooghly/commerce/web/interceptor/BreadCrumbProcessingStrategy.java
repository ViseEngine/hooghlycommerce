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
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStoreView;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.web.ui.Breadcrumb;
import co.hooghly.commerce.web.ui.BreadcrumbItem;
import co.hooghly.commerce.web.ui.BreadcrumbItemType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(4)
public class BreadCrumbProcessingStrategy implements WebInterceptorProcessingStrategy {

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CategoryService categoryService;


	@Override
	public boolean canHandle(String clazz) {
		return StringUtils.equals(clazz, "StoreInterceptor");

	}

	@Override
	public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		log.info("Processing breadcrumb");
		MerchantStoreView merchantStoreView = (MerchantStoreView) request.getAttribute(MERCHANT_STORE_VIEW);
		setBreadcrumb(request, merchantStoreView.computeLocale());
	}

	@Override
	public void postHandle() {
		// TODO Auto-generated method stub

	}

	private void setBreadcrumb(HttpServletRequest request, Locale locale) {

		try {

			// breadcrumb
			Breadcrumb breadCrumb = (Breadcrumb) request.getSession().getAttribute(BREADCRUMB);
			Language language = (Language) request.getAttribute(LANGUAGE);
			if (breadCrumb == null) {
				breadCrumb = new Breadcrumb();
				breadCrumb.setLanguage(language);
				BreadcrumbItem item = getDefaultBreadcrumbItem(locale);
				breadCrumb.getBreadCrumbs().add(item);
			} else {

				// check language
				if (language.getCode().equals(breadCrumb.getLanguage().getCode())) {

					// rebuild using the appropriate language
					List<BreadcrumbItem> items = new ArrayList<BreadcrumbItem>();
					for (BreadcrumbItem item : breadCrumb.getBreadCrumbs()) {

						if (item.getItemType().name().equals(BreadcrumbItemType.HOME)) {
							BreadcrumbItem homeItem = getDefaultBreadcrumbItem(locale);
							homeItem.setItemType(BreadcrumbItemType.HOME);
							homeItem.setLabel(messageSource.getMessage(HOME_MENU_KEY, null, locale));
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

	private BreadcrumbItem getDefaultBreadcrumbItem(Locale locale) {

		// set home page item
		BreadcrumbItem item = new BreadcrumbItem();
		item.setItemType(BreadcrumbItemType.HOME);
		item.setLabel(messageSource.getMessage(HOME_MENU_KEY, null, locale));
		item.setUrl(HOME_URL);
		return item;

	}

}
