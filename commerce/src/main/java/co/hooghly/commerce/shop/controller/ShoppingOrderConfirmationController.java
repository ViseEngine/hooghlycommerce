package co.hooghly.commerce.shop.controller;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.OrderProductDownloadService;
import co.hooghly.commerce.business.OrderService;
import co.hooghly.commerce.business.PaymentService;
import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.business.ShippingService;
import co.hooghly.commerce.business.ShoppingCartService;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Order;
import co.hooghly.commerce.domain.OrderProductDownload;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.facade.CustomerFacade;
import co.hooghly.commerce.facade.OrderFacade;
import co.hooghly.commerce.facade.ShoppingCartFacade;
import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.web.populator.ReadableOrderProductDownloadPopulator;
import co.hooghly.commerce.web.ui.ReadableOrder;
import co.hooghly.commerce.web.ui.ReadableOrderProductDownload;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
@RequestMapping(Constants.SHOP_URI+"/order")
@ConditionalOnProperty(prefix="shop.controller.ShoppingOrderConfirmationController", name="enabled")
public class ShoppingOrderConfirmationController extends AbstractController {
	
	private static final Logger LOGGER = LoggerFactory
	.getLogger(ShoppingOrderConfirmationController.class);
	
	@Inject
	private ShoppingCartFacade shoppingCartFacade;
	
    @Inject
    private ShoppingCartService shoppingCartService;
	

	
	@Inject
	private PaymentService paymentService;
	
	@Inject
	private ShippingService shippingService;
	

	@Inject
	private OrderService orderService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private CountryService countryService;
	
	@Inject
	private ZoneService zoneService;
	
	@Inject
	private OrderFacade orderFacade;
	
	@Inject
	private LabelUtils messages;
	
	@Inject
	private PricingService pricingService;

    @Inject
    private  CustomerFacade customerFacade;
	
	@Inject
    private AuthenticationManager customerAuthenticationManager;
	
	@Inject
	private OrderProductDownloadService orderProdctDownloadService;

	/**
	 * Invoked once the payment is complete and order is fulfilled
	 * @param model
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/confirmation.html")
	public String displayConfirmation(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		Language language = (Language)request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

		Long orderId = super.getSessionAttribute(Constants.ORDER_ID, request);
		if(orderId==null) {
			return new StringBuilder().append("redirect:").append(Constants.SHOP_URI).toString();
		}

		//get the order
		Order order = orderService.getById(orderId);
		if(order == null) {
			LOGGER.warn("Order id [" + orderId + "] does not exist");
			throw new Exception("Order id [" + orderId + "] does not exist");
		}
		
		if(order.getMerchant().getId().intValue()!=store.getId().intValue()) {
			LOGGER.warn("Store id [" + store.getId() + "] differs from order.store.id [" + order.getMerchant().getId() + "]");
			return new StringBuilder().append("redirect:").append(Constants.SHOP_URI).toString();
		}

		if(super.getSessionAttribute(Constants.ORDER_ID_TOKEN, request)!=null) {
			//set this unique token for performing unique operations in the confirmation
			model.addAttribute("confirmation", "confirmation");
		}
		
		//remove unique token
		super.removeAttribute(Constants.ORDER_ID_TOKEN, request);
		
		
        String[] orderMessageParams = {store.getStorename()};
        String orderMessage = messages.getMessage("label.checkout.text", orderMessageParams, locale);
		model.addAttribute("ordermessage", orderMessage);
		
        String[] orderIdParams = {String.valueOf(order.getId())};
        String orderMessageId = messages.getMessage("label.checkout.orderid", orderIdParams, locale);
		model.addAttribute("ordermessageid", orderMessageId);
		
        String[] orderEmailParams = {order.getCustomerEmailAddress()};
        String orderEmailMessage = messages.getMessage("label.checkout.email", orderEmailParams, locale);
		model.addAttribute("orderemail", orderEmailMessage);
		
		ReadableOrder readableOrder = orderFacade.getReadableOrder(orderId, store, language);
		

		
		//resolve country and Zone for GA
		String countryCode = readableOrder.getCustomer().getBilling().getCountry();
		Map<String,Country> countriesMap = countryService.getCountriesMap(language);
		Country billingCountry = countriesMap.get(countryCode);
		if(billingCountry!=null) {
			readableOrder.getCustomer().getBilling().setCountry(billingCountry.getName());
		}
		
		String zoneCode = readableOrder.getCustomer().getBilling().getZone();
		Map<String,Zone> zonesMap = zoneService.getZones(language);
		Zone billingZone = zonesMap.get(zoneCode);
		if(billingZone!=null) {
			readableOrder.getCustomer().getBilling().setZone(billingZone.getName());
		}
		
		
		model.addAttribute("order",readableOrder);
		
		//check if any downloads exist for this order
		List<OrderProductDownload> orderProductDownloads = orderProdctDownloadService.getByOrderId(order.getId());
		if(CollectionUtils.isNotEmpty(orderProductDownloads)) {
			ReadableOrderProductDownloadPopulator populator = new ReadableOrderProductDownloadPopulator();
			List<ReadableOrderProductDownload> downloads = new ArrayList<ReadableOrderProductDownload>();
			for(OrderProductDownload download : orderProductDownloads) {
				ReadableOrderProductDownload view = new ReadableOrderProductDownload();
				populator.populate(download, view,  store, language);
				downloads.add(view);
			}
			model.addAttribute("downloads", downloads);
		}
		
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Checkout.confirmation).append(".").append(store.getStoreTemplate());
		return template.toString();

		
	}
	
	


}
