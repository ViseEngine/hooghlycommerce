package co.hooghly.commerce.admin.controller;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductCriteria;
import co.hooghly.commerce.domain.ProductDescription;
import co.hooghly.commerce.domain.ProductList;
import co.hooghly.commerce.domain.admin.Menu;
import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.web.ui.AjaxPageableResponse;
import co.hooghly.commerce.web.ui.AjaxResponse;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Slf4j
@Controller
@RequestMapping("/admin/secure/products")
public class ProductsController {
	
	@Autowired
	CategoryService categoryService;
	
	@Autowired
	ProductService productService;
	
	@Autowired
	LabelUtils messages;
	
	@GetMapping("/list")
	public String findProductsByCategoryId(MerchantStore store , Language language, @RequestParam(name="categoryId") Long categoryId, Model model)  {
		
		
		List<Product> products = productService.getProducts(Arrays.asList(new Long[]{categoryId}));
		
		model.addAttribute("products", products);
		
		return "admin/products";
		
	}

	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/paging.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageProducts(HttpServletRequest request, HttpServletResponse response) {
		
		//TODO what if ROOT
		
		String categoryId = request.getParameter("categoryId");
		String sku = request.getParameter("sku");
		String available = request.getParameter("available");
		String searchTerm = request.getParameter("searchTerm");
		String name = request.getParameter("name");
		
		AjaxPageableResponse resp = new AjaxPageableResponse();
		
		try {
			
		
			int startRow = Integer.parseInt(request.getParameter("_startRow"));
			int endRow = Integer.parseInt(request.getParameter("_endRow"));
			
			Language language = (Language)request.getAttribute("LANGUAGE");
			MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
			
			ProductCriteria criteria = new ProductCriteria();
			
			criteria.setStartIndex(startRow);
			criteria.setMaxCount(endRow);
			
			
			if(!StringUtils.isBlank(categoryId) && !categoryId.equals("-1")) {
				
				//get other filters
				Long lcategoryId = 0L;
				try {
					lcategoryId = Long.parseLong(categoryId);
				} catch (Exception e) {
					log.error("Product page cannot parse categoryId " + categoryId );
					resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
					String returnString = resp.toJSONString();
					return new ResponseEntity<String>(returnString,HttpStatus.BAD_REQUEST);
				} 
				
				

				if(lcategoryId>0) {
				
					Category category = categoryService.findOne(lcategoryId);
	
					if(category==null || category.getMerchantStore().getId()!=store.getId()) {
						resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
						String returnString = resp.toJSONString();
						return new ResponseEntity<String>(returnString,HttpStatus.BAD_REQUEST);
					}
					
					//get all sub categories
					StringBuilder lineage = new StringBuilder();
					lineage.append(category.getLineage()).append(category.getId()).append("/");
					
					List<Category> categories = categoryService.listByLineage(store, lineage.toString());
					
					List<Long> categoryIds = new ArrayList<Long>();
					
					for(Category cat : categories) {
						categoryIds.add(cat.getId());
					}
					categoryIds.add(category.getId());
					criteria.setCategoryIds(categoryIds);
				
				}
				


				
			}
			
			if(!StringUtils.isBlank(sku)) {
				criteria.setCode(sku);
			}
			
			if(!StringUtils.isBlank(name)) {
				criteria.setProductName(name);
			}
			
			if(!StringUtils.isBlank(available)) {
				if(available.equals("true")) {
					criteria.setAvailable(new Boolean(true));
				} else {
					criteria.setAvailable(new Boolean(false));
				}
			}
			
			ProductList productList = productService.listByStore(store, language, criteria);
			resp.setEndRow(productList.getTotalCount());
			resp.setStartRow(startRow);
			List<Product> plist = productList.getProducts();
			
			if(plist!=null) {
			
				for(Product product : plist) {
					
					Map entry = new HashMap();
					entry.put("productId", product.getId());
					
					ProductDescription description = product.getDescriptions().iterator().next();
					
					entry.put("name", description.getName());
					entry.put("sku", product.getSku());
					entry.put("available", product.isAvailable());
					resp.addDataEntry(entry);
					
					
					
				}
			
			}

			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);
		
		} catch (Exception e) {
			log.error("Error while paging products", e);
			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		return new ResponseEntity<String>(returnString,HttpStatus.OK);


	}
	
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value="/admin/products/remove.html", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteProduct(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		String sid = request.getParameter("productId");

		MerchantStore store = (MerchantStore)request.getAttribute(Constants.ADMIN_STORE);
		
		AjaxResponse resp = new AjaxResponse();

		
		try {
			
			Long id = Long.parseLong(sid);
			
			Product product = productService.getById(id);

			if(product==null || product.getMerchantStore().getId()!=store.getId()) {

				resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);			
				
			} else {
				
				productService.delete(product);
				resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);
				
			}
		
		
		} catch (Exception e) {
			log.error("Error while deleting product", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}
		
		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders= new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new ResponseEntity<String>(returnString,httpHeaders,HttpStatus.OK);
	}
	
	

	
}
