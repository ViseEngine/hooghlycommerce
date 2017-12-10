package co.hooghly.commerce.admin.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.CategoryDescription;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;

import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.web.ui.AjaxResponse;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Controller
public class AdminCategoryController {

	

	@Autowired
	LanguageService languageService;

	@Autowired
	CategoryService categoryService;

	@Autowired
	CountryService countryService;

	@Autowired
	LabelUtils messages;

	@RequestMapping(value = "/admin/categories/editCategory.html", method = RequestMethod.GET)
	public String displayCategoryEdit(@RequestParam("id") long categoryId, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return displayCategory(categoryId, model, request, response);

	}

	@RequestMapping(value = "/admin/categories/createCategory.html", method = RequestMethod.GET)
	public String displayCategoryCreate(Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return displayCategory(null, model, request, response);

	}

	private String displayCategory(Long categoryId, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language) request.getAttribute("LANGUAGE");

		// get parent categories
		List<Category> categories = categoryService.listByStore(store, language);

		List<Language> languages = store.getLanguages();
		Category category = new Category();

		if (categoryId != null && categoryId != 0) {// edit mode
			category = categoryService.getById(categoryId);

			if (category == null || category.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				return "catalogue-categories";
			}
		} else {

			category.setVisible(true);

		}

		List<CategoryDescription> descriptions = new ArrayList<CategoryDescription>();

		for (Language l : languages) {

			CategoryDescription description = null;
			if (category != null) {
				for (CategoryDescription desc : category.getDescriptions()) {

					if (desc.getLanguage().getCode().equals(l.getCode())) {
						description = desc;
					}

				}
			}

			if (description == null) {
				description = new CategoryDescription();
				description.setLanguage(l);
			}

			descriptions.add(description);

		}

		category.setDescriptions(descriptions);

		model.addAttribute("category", category);
		model.addAttribute("categories", categories);

		return "catalogue-categories-category";
	}

	@RequestMapping(value = "/admin/categories/save.html", method = RequestMethod.POST)
	public String saveCategory(@Valid @ModelAttribute("category") Category category, BindingResult result, Model model,
			HttpServletRequest request) throws Exception {

		Language language = (Language) request.getAttribute("LANGUAGE");

		

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		if (category.getId() != null && category.getId() > 0) { // edit entry

			// get from DB
			Category currentCategory = categoryService.getById(category.getId());

			if (currentCategory == null
					|| currentCategory.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				return "catalogue-categories";
			}

		}

		Map<String, Language> langs = languageService.getLanguagesMap();

		List<CategoryDescription> descriptions = category.getDescriptions();
		if (descriptions != null) {

			for (CategoryDescription description : descriptions) {

				String code = description.getLanguage().getCode();
				Language l = langs.get(code);
				description.setLanguage(l);
				description.setCategory(category);

			}

		}

		// save to DB
		category.setMerchantStore(store);
		// }

		if (result.hasErrors()) {
			return "catalogue-categories-category";
		}

		// check parent
		if (category.getParent() != null) {
			if (category.getParent().getId() == -1) {// this is a root category
				category.setParent(null);
				category.setLineage("/");
				category.setDepth(0);
			}
		}

		category.getAuditSection().setModifiedBy(request.getRemoteUser());
		categoryService.saveOrUpdate(category);

		// ajust lineage and depth
		if (category.getParent() != null && category.getParent().getId() != -1) {

			Category parent = new Category();
			parent.setId(category.getParent().getId());
			parent.setMerchantStore(store);

			categoryService.addChild(parent, category);

		}

		// get parent categories
		List<Category> categories = categoryService.listByStore(store, language);
		model.addAttribute("categories", categories);

		model.addAttribute("success", "success");
		return "catalogue-categories-category";
	}

	// category list
	@GetMapping(value = "/admin/secure/category")
	public String displayCategories(Model model, HttpServletRequest request, HttpServletResponse response) {

		return "admin/categories";
	}

	@GetMapping(value = "/admin/secure/category/list")
	public @ResponseBody ResponseEntity<List<Map>> list(@RequestParam(name="parent",defaultValue="0") Long parentId, HttpServletRequest request
			) {
		
		log.info("Parent id - {}", parentId);
		Language language = (Language) request.getAttribute("LANGUAGE");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		log.info("M Store = " + store);

		List<Category> categories = null;

		if (parentId != 0L) {
			
			Category parent = categoryService.findOne(parentId);
			if(parent != null){			
				categories = parent.getCategories();
			}	
		} else {
			
			categories = categoryService.findByStoreAndParent(store, null);

		}

		List<Map> categoryList = new ArrayList<>();
		for (Category category : categories) {
			
			categoryList.add(convert(category));
			
		}

		return new ResponseEntity<List<Map>>(categoryList, HttpStatus.OK);
	}
	
	private Map convert(Category category) {
		Map entry = new HashMap();
		
		CategoryDescription description = category.getDescriptions().get(0);

		entry.put("title", description.getName());
		entry.put("key", category.getId() + "");
		
		
		List<Category> children = category.getCategories();
		//look only 1 level down for children
		if(children != null && !children.isEmpty()) {
			List<Map> childrenList = new ArrayList<Map>();
			for(Category c : children) {
				Map childEntry = new HashMap();
				
				CategoryDescription childDescription = c.getDescriptions().get(0);

				childEntry.put("title", childDescription.getName());
				childEntry.put("key", c.getId() + "");
				childEntry.put("lazy", true);
				
				childrenList.add(childEntry);
			}
			
			entry.put("children", childrenList);
		}
		else{
			entry.put("lazy", false);
			entry.put("children", null);
		}
		return entry;
	}

	@Deprecated
	@SuppressWarnings({ "unchecked" })
	@RequestMapping(value = "/admin/categories/paging.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageCategories(HttpServletRequest request,
			HttpServletResponse response) {
		String categoryName = request.getParameter("name");
		String categoryCode = request.getParameter("code");

		AjaxResponse resp = new AjaxResponse();

		try {

			Language language = (Language) request.getAttribute("LANGUAGE");

			MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

			List<Category> categories = null;

			if (!StringUtils.isBlank(categoryName)) {

				categories = categoryService.getByName(store, categoryName, language);

			} else if (!StringUtils.isBlank(categoryCode)) {

				categoryService.listByCodes(store, new ArrayList<String>(Arrays.asList(categoryCode)), language);

			} else {

				categories = categoryService.listByStore(store, language);

			}

			for (Category category : categories) {

				@SuppressWarnings("rawtypes")
				Map entry = new HashMap();
				entry.put("categoryId", category.getId());

				CategoryDescription description = category.getDescriptions().get(0);

				entry.put("name", description.getName());
				entry.put("code", category.getCode());
				entry.put("visible", category.isVisible());
				resp.addDataEntry(entry);

			}

			resp.setStatus(AjaxResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			log.error("Error while paging categories", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
		}

		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value = "/admin/categories/hierarchy.html", method = RequestMethod.GET)
	public String displayCategoryHierarchy(Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

	

		// get the list of categories
		Language language = (Language) request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		List<Category> categories = categoryService.listByStore(store, language);

		model.addAttribute("categories", categories);

		return "catalogue-categories-hierarchy";
	}

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value = "/admin/categories/remove.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteCategory(HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		String sid = request.getParameter("categoryId");

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		AjaxResponse resp = new AjaxResponse();

		try {

			Long id = Long.parseLong(sid);

			Category category = categoryService.getById(id);

			if (category == null || category.getMerchantStore().getId().intValue() != store.getId().intValue()) {

				resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);

			} else {

				categoryService.delete(category);
				resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

			}

		} catch (Exception e) {
			log.error("Error while deleting category", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}

		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value = "/admin/categories/moveCategory.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> moveCategory(HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		String parentid = request.getParameter("parentId");
		String childid = request.getParameter("childId");

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		AjaxResponse resp = new AjaxResponse();
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		try {

			Long parentId = Long.parseLong(parentid);
			Long childId = Long.parseLong(childid);

			Category child = categoryService.getById(childId);
			Category parent = categoryService.getById(parentId);

			if (child.getParent().getId() == parentId) {
				resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
				String returnString = resp.toJSONString();
			}

			if (parentId != 1) {

				if (child == null || parent == null || child.getMerchantStore().getId() != store.getId()
						|| parent.getMerchantStore().getId() != store.getId()) {
					resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));

					resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					String returnString = resp.toJSONString();
					return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
				}

				if (child.getMerchantStore().getId() != store.getId()
						|| parent.getMerchantStore().getId() != store.getId()) {
					resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
					resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					String returnString = resp.toJSONString();
					return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
				}

			}

			parent.getAuditSection().setModifiedBy(request.getRemoteUser());
			categoryService.addChild(parent, child);
			resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			log.error("Error while moving category", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}

		String returnString = resp.toJSONString();

		return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value = "/admin/categories/checkCategoryCode.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> checkCategoryCode(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		String code = request.getParameter("code");
		String id = request.getParameter("id");

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		AjaxResponse resp = new AjaxResponse();

		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		if (StringUtils.isBlank(code)) {
			resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
			String returnString = resp.toJSONString();
			return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
		}

		try {

			Category category = categoryService.getByCode(store, code);

			if (category != null && StringUtils.isBlank(id)) {
				resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
				String returnString = resp.toJSONString();
				return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
			}

			if (category != null && !StringUtils.isBlank(id)) {
				try {
					Long lid = Long.parseLong(id);

					if (category.getCode().equals(code) && category.getId().longValue() == lid) {
						resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
						String returnString = resp.toJSONString();
						return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
					}
				} catch (Exception e) {
					resp.setStatus(AjaxResponse.CODE_ALREADY_EXIST);
					String returnString = resp.toJSONString();
					return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
				}

			}

			resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			log.error("Error while getting category", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}

		String returnString = resp.toJSONString();

		return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
	}



}
