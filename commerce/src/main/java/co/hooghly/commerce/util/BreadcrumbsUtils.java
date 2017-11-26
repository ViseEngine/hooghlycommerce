package co.hooghly.commerce.util;


import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.web.ui.Breadcrumb;
import co.hooghly.commerce.web.ui.BreadcrumbItem;
import co.hooghly.commerce.web.ui.BreadcrumbItemType;
import co.hooghly.commerce.web.ui.ReadableCategory;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
public class BreadcrumbsUtils {
	
	@Inject
	private LabelUtils messages;
	
	@Inject
	private CategoryService categoryService;
	
	@Inject
	private FilePathUtils filePathUtils;
	
	
	public Breadcrumb buildCategoryBreadcrumb(ReadableCategory categoryClicked, MerchantStore store, Language language, String contextPath) throws Exception {
		
		/** Rebuild breadcrumb **/
		BreadcrumbItem home = new BreadcrumbItem();
		home.setItemType(BreadcrumbItemType.HOME);
		home.setLabel(messages.getMessage(Constants.HOME_MENU_KEY, LocaleUtils.getLocale(language)));
		home.setUrl(filePathUtils.buildStoreUri(store, contextPath) + Constants.SHOP_URI);

		Breadcrumb breadCrumb = new Breadcrumb();
		breadCrumb.setLanguage(language);
		
		List<BreadcrumbItem> items = new ArrayList<BreadcrumbItem>();
		items.add(home);
		
		//if(!StringUtils.isBlank(refContent)) {

			//List<String> categoryIds = parseBreadCrumb(refContent);
			List<String> categoryIds = parseCategoryLineage(categoryClicked.getLineage());
			List<Long> ids = new ArrayList<Long>();
			for(String c : categoryIds) {
				ids.add(Long.parseLong(c));
			}
			
			ids.add(categoryClicked.getId());
			
			
			List<Category> categories = categoryService.listByIds(store, ids, language);
			
			//category path - use lineage
			for(Category c : categories) {
				BreadcrumbItem categoryBreadcrump = new BreadcrumbItem();
				categoryBreadcrump.setItemType(BreadcrumbItemType.CATEGORY);
				categoryBreadcrump.setLabel(c.getDescription().getName());
				categoryBreadcrump.setUrl(filePathUtils.buildCategoryUrl(store, contextPath, c.getDescription().getSeUrl()));
				items.add(categoryBreadcrump);
			}
			
			breadCrumb.setUrlRefContent(buildBreadCrumb(ids));
			
		//}
		


		breadCrumb.setBreadCrumbs(items);
		breadCrumb.setItemType(BreadcrumbItemType.CATEGORY);
		
		
		return breadCrumb;
	}
	
	
	public Breadcrumb buildProductBreadcrumb(String refContent, Product productClicked, MerchantStore store, Language language, String contextPath) throws Exception {
		
		/** Rebuild breadcrumb **/
		BreadcrumbItem home = new BreadcrumbItem();
		home.setItemType(BreadcrumbItemType.HOME);
		home.setLabel(messages.getMessage(Constants.HOME_MENU_KEY, LocaleUtils.getLocale(language)));
		home.setUrl(filePathUtils.buildStoreUri(store, contextPath) + Constants.SHOP_URI);

		Breadcrumb breadCrumb = new Breadcrumb();
		breadCrumb.setLanguage(language);
		
		List<BreadcrumbItem> items = new ArrayList<BreadcrumbItem>();
		items.add(home);
		
		if(!StringUtils.isBlank(refContent)) {

			List<String> categoryIds = parseBreadCrumb(refContent);
			List<Long> ids = new ArrayList<Long>();
			for(String c : categoryIds) {
				ids.add(Long.parseLong(c));
			}
			
			
			List<Category> categories = categoryService.listByIds(store, ids, language);
			
			//category path - use lineage
			for(Category c : categories) {
				BreadcrumbItem categoryBreadcrump = new BreadcrumbItem();
				categoryBreadcrump.setItemType(BreadcrumbItemType.CATEGORY);
				categoryBreadcrump.setLabel(c.getDescription().getName());
				categoryBreadcrump.setUrl(filePathUtils.buildCategoryUrl(store, contextPath, c.getDescription().getSeUrl()));
				items.add(categoryBreadcrump);
			}
			

			breadCrumb.setUrlRefContent(buildBreadCrumb(ids));
		} 
		
		BreadcrumbItem productBreadcrump = new BreadcrumbItem();
		productBreadcrump.setItemType(BreadcrumbItemType.PRODUCT);
		productBreadcrump.setLabel(productClicked.getProductDescription().getName());
		productBreadcrump.setUrl(filePathUtils.buildProductUrl(store, contextPath, productClicked.getProductDescription().getSeUrl()));
		items.add(productBreadcrump);
		
		
		


		breadCrumb.setBreadCrumbs(items);
		breadCrumb.setItemType(BreadcrumbItemType.CATEGORY);
		
		
		return breadCrumb;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	private List<String> parseBreadCrumb(String refContent) throws Exception {
		
		/** c:1,2,3 **/
		String[] categoryComa = refContent.split(":");
		String[] categoryIds = categoryComa[1].split(",");
		return new LinkedList(Arrays.asList(categoryIds));
		
		
	}
	

	private List<String> parseCategoryLineage(String lineage) throws Exception {
		
		String[] categoryPath = lineage.split(Constants.CATEGORY_LINEAGE_DELIMITER);
		List<String> returnList = new LinkedList<String>();
		for(String c : categoryPath) {
			if(!StringUtils.isBlank(c)) {
				returnList.add(c);
			}
		}
		return returnList;

	}
	
	private String buildBreadCrumb(List<Long> ids) throws Exception {
		
		if(CollectionUtils.isEmpty(ids)) {
			return null;
		}
			StringBuilder sb = new StringBuilder();
			sb.append("c:");
			int count = 1;
			for(Long c : ids) {
				sb.append(c);
				if(count < ids.size()) {
					sb.append(",");
				}
				count++;
			}
		
		
		return sb.toString();
		
	}

}
