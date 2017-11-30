package co.hooghly.commerce.api;


import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.facade.CategoryFacade;
import co.hooghly.commerce.web.populator.ReadableCategoryPopulator;
import co.hooghly.commerce.web.ui.PersistableCategory;
import co.hooghly.commerce.web.ui.ReadableCategory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

/**
 * Rest services for category management
 * 
 *
 */
@Controller
@RequestMapping("/api")
public class ShoppingCategoryController {
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private MerchantStoreService merchantStoreService;
	
	@Inject
	private CategoryService categoryService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private CategoryFacade categoryFacade;
	

	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCategoryController.class);
	

	
	@RequestMapping( value="/public/{store}/category/{id}", method=RequestMethod.GET)
	@ResponseBody
	public ReadableCategory getCategory(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response) {
		
		
		try {
			
			/** default routine **/
			
			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			if(merchantStore!=null) {
				if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}
			
			Language language = merchantStore.getDefaultLanguage();
			
			Map<String,Language> langs = languageService.getLanguagesMap();

			
			if(!StringUtils.isBlank(request.getParameter(Constants.LANG))) {
				String lang = request.getParameter(Constants.LANG);
				if(lang!=null) {
					language = langs.get(language);
				}
			}
			
			if(language==null) {
				language = merchantStore.getDefaultLanguage();
			}
			
			
			/** end default routine **/

			
			Category dbCategory = categoryService.getByLanguage(id, language);
			
			if(dbCategory==null) {
				response.sendError(503,  "Invalid category id");
				return null;
			}
			
			if(dbCategory.getMerchantStore().getId().intValue()!=merchantStore.getId().intValue()){
				response.sendError(503, "Invalid category id");
				return null;
			}
			

			ReadableCategoryPopulator populator = new ReadableCategoryPopulator();

			//TODO count products by category
			ReadableCategory category = populator.populate(dbCategory, new ReadableCategory(), merchantStore, merchantStore.getDefaultLanguage());

			return category;
		
		} catch (Exception e) {
			LOGGER.error("Error while saving category",e);
			try {
				response.sendError(503, "Error while saving category " + e.getMessage());
			} catch (Exception ignore) {
			}
			return null;
		}
	}
	

	
	
	/**
	 * Create new category for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/category", method=RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public PersistableCategory createCategory(@PathVariable final String store, @Valid @RequestBody PersistableCategory category, HttpServletRequest request, HttpServletResponse response) {
		
		
		try {


			MerchantStore merchantStore = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);
			if(merchantStore!=null) {
				if(!merchantStore.getCode().equals(store)) {
					merchantStore = null;
				}
			}
			
			if(merchantStore== null) {
				merchantStore = merchantStoreService.getByCode(store);
			}
			
			if(merchantStore==null) {
				LOGGER.error("Merchant store is null for code " + store);
				response.sendError(503, "Merchant store is null for code " + store);
				return null;
			}
			
			categoryFacade.saveCategory(merchantStore, category);

			
			category.setId(category.getId());

			return category;
		
		} catch (Exception e) {
			LOGGER.error("Error while saving category",e);
			try {
				response.sendError(503, "Error while saving category " + e.getMessage());
			} catch (Exception ignore) {
			}
			return null;
		}
	}
	

	
	/**
	 * Deletes a category for a given MerchantStore
	 */
	@RequestMapping( value="/private/{store}/category/{id}", method=RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteCategory(@PathVariable final String store, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Category category = categoryService.getById(id);
		if(category != null && category.getMerchantStore().getCode().equalsIgnoreCase(store)){
			categoryService.delete(category);
		}else{
			response.sendError(404, "No Category found for ID : " + id);
		}
	}

	
	

	
}
