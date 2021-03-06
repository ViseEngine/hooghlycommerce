package co.hooghly.commerce.api;

import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.business.DigitalProductService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.OrderService;
import co.hooghly.commerce.business.ProductAttributeService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Order;
import co.hooghly.commerce.domain.OrderCriteria;
import co.hooghly.commerce.facade.OrderFacade;
import co.hooghly.commerce.web.populator.CustomerPopulator;
import co.hooghly.commerce.web.populator.PersistableOrderPopulator;
import co.hooghly.commerce.web.ui.PersistableCustomer;
import co.hooghly.commerce.web.ui.PersistableOrder;
import co.hooghly.commerce.web.ui.ReadableOrderList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/api/{store}/orders")
public class OrderController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);
	
	
	@Autowired
	private MerchantStoreService merchantStoreService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductAttributeService productAttributeService;
	
	@Autowired
	private DigitalProductService digitalProductService;
	
	@Autowired
	private OrderFacade orderFacade;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private LanguageService languageService;

	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public Order createOrder(@PathVariable final String store, @Valid @RequestBody Order order, HttpServletRequest request, HttpServletResponse response) {
		MerchantStore merchantStore = merchantStoreService.getByCode(store);
		//Saving order should be able to save customer if not new. need to test
	
		return orderService.save(order);
		
	}
	
	
	/**
	 * Get a list of orders
	 * accept request parameter 'lang' [en,fr...] otherwise store dafault language
	 * accept request parameter 'start' start index for count
	 * accept request parameter 'max' maximum number count, otherwise returns all
	 * @param store
	 * @param order
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping( value="/{store}/orders/")
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ResponseBody
	public Order listOrders(@PathVariable final String store, @RequestParam("lang") String lang, @RequestParam("start") int start, @RequestParam("max") int max)  {
		//Need to handle pagination
		MerchantStore merchantStore = merchantStoreService.getByCode(store);
		
		
		/*if(StringUtils.isBlank(lang)) {
			lang = merchantStore.getDefaultLanguage().getCode();
		}*/
		
		
		Language language = languageService.getByCode(lang);
		
		
		
		
		
		
		//ReadableOrderList returnList = orderFacade.getReadableOrderList(merchantStore, startCount, maxCount, language);

		return null;
	}
	
	/**
	 * Get a list of orders for a given customer
	 * accept request parameter 'lang' [en,fr...] otherwise store dafault language
	 * accept request parameter 'start' start index for count
	 * accept request parameter 'max' maximum number count, otherwise returns all
	 * @param store
	 * @param order
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping( value="/{store}/orders/customer/{id}", method=RequestMethod.GET)
	@ResponseStatus(HttpStatus.ACCEPTED)
	@ResponseBody
	public ReadableOrderList listOrders(@PathVariable final String store, @PathVariable final Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
		
		//get additional request parameters for orders
		String lang = request.getParameter(Constants.LANG);		
		String start = request.getParameter(Constants.START);
		String max = request.getParameter(Constants.MAX);
		
		int startCount = 0;
		int maxCount = 0;
		
		if(StringUtils.isBlank(lang)) {
			lang = merchantStore.getDefaultLanguage().getCode();
		}
		
		
		Language language = languageService.getByCode(lang);
		
		if(language==null) {
			LOGGER.error("Language is null for code " + lang);
			response.sendError(503, "Language is null for code " + lang);
			return null;
		}
		
		try {
			startCount = Integer.parseInt(start);
		} catch (Exception e) {
			LOGGER.info("Invalid value for start " + start);
		}
		
		try {
			maxCount = Integer.parseInt(max);
		} catch (Exception e) {
			LOGGER.info("Invalid value for max " + max);
		}
		
		Customer customer = customerService.getById(id);
		
		if(customer==null) {
			LOGGER.error("Customer is null for id " + id);
			response.sendError(503, "Customer is null for id " + id);
			return null;
		}
		
		if(customer.getMerchantStore().getId().intValue()!=merchantStore.getId().intValue()) {
			LOGGER.error("Customer is null for id " + id + " and store id " + store);
			response.sendError(503, "Customer is null for id " + id + " and store id " + store);
			return null;
		}
		
		ReadableOrderList returnList = orderFacade.getReadableOrderList(merchantStore, startCount, maxCount, language);

		return returnList;
	}

}
