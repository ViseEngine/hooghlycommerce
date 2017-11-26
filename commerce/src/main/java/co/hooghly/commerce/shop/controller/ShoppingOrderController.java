package co.hooghly.commerce.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.hooghly.commerce.business.CountryService;
import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.business.OrderProductDownloadService;
import co.hooghly.commerce.business.OrderService;
import co.hooghly.commerce.business.PaymentService;
import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.business.ShippingService;
import co.hooghly.commerce.business.ShoppingCartService;
import co.hooghly.commerce.business.ZoneService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Billing;
import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Order;
import co.hooghly.commerce.domain.OrderProductDownload;
import co.hooghly.commerce.domain.OrderTotal;
import co.hooghly.commerce.domain.OrderTotalSummary;
import co.hooghly.commerce.domain.PaymentMethod;
import co.hooghly.commerce.domain.ShippingMetaData;
import co.hooghly.commerce.domain.ShippingOption;
import co.hooghly.commerce.domain.ShippingQuote;
import co.hooghly.commerce.domain.ShippingSummary;
import co.hooghly.commerce.domain.ShoppingCart;
import co.hooghly.commerce.domain.ShoppingCartItem;
import co.hooghly.commerce.domain.Transaction;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.domain.admin.UserReset;
import co.hooghly.commerce.facade.CustomerFacade;
import co.hooghly.commerce.facade.OrderFacade;
import co.hooghly.commerce.facade.ShoppingCartFacade;
import co.hooghly.commerce.util.EmailTemplatesUtils;
import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.web.populator.ReadableCustomerDeliveryAddressPopulator;
import co.hooghly.commerce.web.populator.ReadableOrderTotalPopulator;
import co.hooghly.commerce.web.populator.ReadableShippingSummaryPopulator;
import co.hooghly.commerce.web.populator.ReadableShopOrderPopulator;
import co.hooghly.commerce.web.ui.AnonymousCustomer;
import co.hooghly.commerce.web.ui.PersistableCustomer;
import co.hooghly.commerce.web.ui.ReadableDelivery;
import co.hooghly.commerce.web.ui.ReadableOrderTotal;
import co.hooghly.commerce.web.ui.ReadableShippingSummary;
import co.hooghly.commerce.web.ui.ReadableShopOrder;
import co.hooghly.commerce.web.ui.ShopOrder;
import co.hooghly.commerce.web.ui.ShoppingCartData;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.*;

/**
 * Displays checkout form and deals with ajax user input
 * 
 *
 */
@Controller
@RequestMapping(Constants.SHOP_URI + "/order")
@Slf4j
@ConditionalOnProperty(prefix="shop.controller.ShoppingOrderController", name="enabled")
public class ShoppingOrderController extends AbstractController {

	@Inject
	private ShoppingCartFacade shoppingCartFacade;

	@Inject
	private ShoppingCartService shoppingCartService;

	@Inject
	private PaymentService paymentService;

	@Inject
	private CustomerService customerService;

	@Inject
	private ShippingService shippingService;

	@Inject
	private OrderService orderService;

	@Inject
	private CountryService countryService;

	@Inject
	private ZoneService zoneService;

	@Inject
	private OrderFacade orderFacade;

	@Inject
	private CustomerFacade customerFacade;

	@Inject
	private LabelUtils messages;

	@Inject
	private PricingService pricingService;

	@Inject
	private PasswordEncoder passwordEncoder;

	@Inject
	private EmailTemplatesUtils emailTemplatesUtils;

	@Inject
	private OrderProductDownloadService orderProdctDownloadService;

	private static final String REDIRECT_URL = "redirect:/shop/cart/shoppingCart.html";

	@RequestMapping("/checkout")
	public String displayCheckout(Language language, MerchantStore store, Customer customer,
			@CookieValue("cart") String cookie, Model model, HttpServletRequest request, Locale locale)
			throws Exception {

		/**
		 * Shopping cart
		 * 
		 * ShoppingCart should be in the HttpSession Otherwise the cart id is in
		 * the cookie Otherwise the customer is in the session and a cart exist
		 * in the DB Else -> Nothing to display
		 */

		// Get the cart from the DB
		String shoppingCartCode = (String) request.getSession().getAttribute(Constants.SHOPPING_CART);
		ShoppingCart cart = shoppingCartService.findCartForCheckout(cookie, shoppingCartCode, store, customer);

		if (cart == null || (customer != null && (cart.getCustomerId() != customer.getId().longValue()))) {
			return REDIRECT_URL;
		}

		else {
			// cart sorted out now time to sort out customer and other records
			super.setSessionAttribute(Constants.SHOPPING_CART, cart.getShoppingCartCode(), request);
			customer = Customer.getInstance(store);
			AnonymousCustomer anonymousCustomer = (AnonymousCustomer) request
					.getAttribute(Constants.ANONYMOUS_CUSTOMER);
			if (anonymousCustomer != null && anonymousCustomer.getBilling() != null) {
				Billing billing = customer.getBilling();
				billing.setCity(anonymousCustomer.getBilling().getCity());
				Map<String, Country> countriesMap = countryService.getCountriesMap(language);
				Country anonymousCountry = countriesMap.get(anonymousCustomer.getBilling().getCountry());
				if (anonymousCountry != null) {
					billing.setCountry(anonymousCountry);
				}
				Map<String, Zone> zonesMap = zoneService.getZones(language);
				Zone anonymousZone = zonesMap.get(anonymousCustomer.getBilling().getZone());
				if (anonymousZone != null) {
					billing.setZone(anonymousZone);
				}
				if (anonymousCustomer.getBilling().getPostalCode() != null) {
					billing.setPostalCode(anonymousCustomer.getBilling().getPostalCode());
				}
				customer.setBilling(billing);
			}
		}

		// 1.check if an existing order exist
		ShopOrder order = super.getSessionAttribute(Constants.ORDER, request);

		if (order == null) {
			order = orderFacade.initializeOrder(store, customer, cart, language);
		}

		boolean freeShoppingCart = shoppingCartService.isFreeShoppingCart(cart);
		boolean requiresShipping = shoppingCartService.requiresShipping(cart);

		/**
		 * hook for displaying or not delivery address configuration
		 */
		ShippingMetaData shippingMetaData = shippingService.getShippingMetaData(store);
		model.addAttribute("shippingMetaData", shippingMetaData);// TODO DTO

		/** shipping **/
		ShippingQuote quote = null;
		if (requiresShipping) {

			// Get all applicable shipping quotes
			quote = orderFacade.getShippingQuote(customer, cart, order, store, language);
			model.addAttribute("shippingQuote", quote);
		}

		if (quote != null) {
			String shippingReturnCode = quote.getShippingReturnCode();

			if (StringUtils.isBlank(shippingReturnCode) || shippingReturnCode.equals(ShippingQuote.NO_POSTAL_CODE)) {

				if (order.getShippingSummary() == null) {
					ShippingSummary summary = orderFacade.getShippingSummary(quote, store, language);
					order.setShippingSummary(summary);
					request.getSession().setAttribute(Constants.SHIPPING_SUMMARY, summary);// TODO
																							// DTO
				}
				if (order.getSelectedShippingOption() == null) {
					order.setSelectedShippingOption(quote.getSelectedShippingOption());
				}

				// save quotes in HttpSession
				List<ShippingOption> options = quote.getShippingOptions();
				request.getSession().setAttribute(Constants.SHIPPING_OPTIONS, options);// TODO
																						// DTO

				if (!CollectionUtils.isEmpty(options)) {

					for (ShippingOption shipOption : options) {

						StringBuilder moduleName = new StringBuilder();
						moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());

						String carrier = messages.getMessage(moduleName.toString(), locale);
						String note = messages.getMessage(moduleName.append(".note").toString(), locale, "");

						shipOption.setDescription(carrier);
						shipOption.setNote(note);

						// option name
						if (!StringUtils.isBlank(shipOption.getOptionCode())) {
							// try to get the translate
							StringBuilder optionCodeBuilder = new StringBuilder();
							try {

								optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode());
								String optionName = messages.getMessage(optionCodeBuilder.toString(), locale);
								shipOption.setOptionName(optionName);
							} catch (Exception e) {// label not found
								log.warn("No shipping code found for " + optionCodeBuilder.toString());
							}
						}

					}

				}

			}

			if (quote.getDeliveryAddress() != null) {
				ReadableCustomerDeliveryAddressPopulator addressPopulator = new ReadableCustomerDeliveryAddressPopulator();
				addressPopulator.setCountryService(countryService);
				addressPopulator.setZoneService(zoneService);
				ReadableDelivery deliveryAddress = new ReadableDelivery();
				addressPopulator.populate(quote.getDeliveryAddress(), deliveryAddress, store, language);
				model.addAttribute("deliveryAddress", deliveryAddress);
				super.setSessionAttribute(Constants.KEY_SESSION_ADDRESS, deliveryAddress, request);
			}

			// get shipping countries
			List<Country> shippingCountriesList = orderFacade.getShipToCountry(store, language);
			model.addAttribute("countries", shippingCountriesList);
		} else {
			// get all countries
			List<Country> countries = countryService.getCountries(language);
			model.addAttribute("countries", countries);
		}

		if (quote != null && quote.getShippingReturnCode() != null
				&& quote.getShippingReturnCode().equals(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED)) {
			log.error("Shipping quote error " + quote.getShippingReturnCode());
			model.addAttribute("errorMessages", quote.getShippingReturnCode());
		}

		if (quote != null && !StringUtils.isBlank(quote.getQuoteError())) {
			log.error("Shipping quote error " + quote.getQuoteError());
			model.addAttribute("errorMessages", quote.getQuoteError());
		}

		if (quote != null && quote.getShippingReturnCode() != null
				&& quote.getShippingReturnCode().equals(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY)) {
			log.error("Shipping quote error " + quote.getShippingReturnCode());
			model.addAttribute("errorMessages", quote.getShippingReturnCode());
		}
		/** end shipping **/

		// get payment methods
		List<PaymentMethod> paymentMethods = paymentService.getAcceptedPaymentMethods(store);

		// not free and no payment methods
		if (CollectionUtils.isEmpty(paymentMethods) && !freeShoppingCart) {
			log.error("No payment method configured");
			model.addAttribute("errorMessages", "No payments configured");
		}

		if (!CollectionUtils.isEmpty(paymentMethods)) {// select default payment
														// method
			PaymentMethod defaultPaymentSelected = null;
			for (PaymentMethod paymentMethod : paymentMethods) {
				if (paymentMethod.isDefaultSelected()) {
					defaultPaymentSelected = paymentMethod;
					break;
				}
			}

			if (defaultPaymentSelected == null) {// forced default selection
				defaultPaymentSelected = paymentMethods.get(0);
				defaultPaymentSelected.setDefaultSelected(true);
			}

			order.setDefaultPaymentMethodCode(defaultPaymentSelected.getPaymentMethodCode());

		}

		// readable shopping cart items for order summary box
		ShoppingCartData shoppingCart = shoppingCartFacade.getShoppingCartData(cart);
		model.addAttribute("cart", shoppingCart);
		// TODO filter here

		// order total
		OrderTotalSummary orderTotalSummary = orderFacade.calculateOrderTotal(store, order, language);
		order.setOrderTotalSummary(orderTotalSummary);
		// if order summary has to be re-used
		super.setSessionAttribute(Constants.ORDER_SUMMARY, orderTotalSummary, request);

		model.addAttribute("order", order);
		model.addAttribute("paymentMethods", paymentMethods);

		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Checkout.checkout).append(".")
				.append(store.getStoreTemplate());
		return template.toString();

	}

	@RequestMapping("/commitPreAuthorized.html")
	public String commitPreAuthorizedOrder(Model model, HttpServletRequest request, HttpServletResponse response,
			Locale locale) throws Exception {

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language) request.getAttribute("LANGUAGE");
		ShopOrder order = super.getSessionAttribute(Constants.ORDER, request);
		if (order == null) {
			StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Pages.timeout).append(".")
					.append(store.getStoreTemplate());
			return template.toString();
		}

		@SuppressWarnings("unchecked")
		Map<String, Object> configs = (Map<String, Object>) request.getAttribute(Constants.REQUEST_CONFIGS);

		if (configs != null && configs.containsKey(Constants.DEBUG_MODE)) {
			Boolean debugMode = (Boolean) configs.get(Constants.DEBUG_MODE);
			if (debugMode) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonInString = mapper.writeValueAsString(order);
					log.debug("Commit pre-authorized order -> " + jsonInString);
				} catch (Exception de) {
					log.error(de.getMessage());
				}
			}
		}

		try {

			OrderTotalSummary totalSummary = super.getSessionAttribute(Constants.ORDER_SUMMARY, request);

			if (totalSummary == null) {
				totalSummary = orderFacade.calculateOrderTotal(store, order, language);
				super.setSessionAttribute(Constants.ORDER_SUMMARY, totalSummary, request);
			}

			order.setOrderTotalSummary(totalSummary);

			// already validated, proceed with commit
			Order orderModel = this.commitOrder(order, request, locale);
			super.setSessionAttribute(Constants.ORDER_ID, orderModel.getId(), request);

			return "redirect:/shop/order/confirmation.html";

		} catch (Exception e) {
			log.error("Error while commiting order", e);
			throw e;

		}

	}

	private Order commitOrder(ShopOrder order, HttpServletRequest request, Locale locale)
			throws Exception, ServiceException {

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language) request.getAttribute("LANGUAGE");

		String userName = null;
		String password = null;

		PersistableCustomer customer = order.getCustomer();

		/** set username and password to persistable object **/
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Customer authCustomer = null;
		if (auth != null && request.isUserInRole("AUTH_CUSTOMER")) {
			authCustomer = customerFacade.getCustomerByUserName(auth.getName(), store);
			// set id and authentication information
			customer.setUserName(authCustomer.getNick());
			customer.setEncodedPassword(authCustomer.getPassword());
			customer.setId(authCustomer.getId());
		} else {
			// set customer id to null
			customer.setId(null);
		}

		// if the customer is new, generate a password
		if (customer.getId() == null || customer.getId() == 0) {// new customer
			password = UserReset.generateRandomString();
			String encodedPassword = passwordEncoder.encode(password);
			customer.setEncodedPassword(encodedPassword);
		}

		if (order.isShipToBillingAdress()) {
			customer.setDelivery(customer.getBilling());
		}

		Customer modelCustomer = null;
		try {// set groups
			if (authCustomer == null) {// not authenticated, create a new
										// volatile user
				modelCustomer = customerFacade.getCustomerModel(customer, store, language);
				customerFacade.setCustomerModelDefaultProperties(modelCustomer, store);
				userName = modelCustomer.getNick();
				log.debug("About to persist volatile customer to database.");
				customerService.saveOrUpdate(modelCustomer);
			} else {// use existing customer
				modelCustomer = customerFacade.populateCustomerModel(authCustomer, customer, store, language);
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		Order modelOrder = null;
		Transaction initialTransaction = (Transaction) super.getSessionAttribute(Constants.INIT_TRANSACTION_KEY,
				request);
		if (initialTransaction != null) {
			modelOrder = orderFacade.processOrder(order, modelCustomer, initialTransaction, store, language);
		} else {
			modelOrder = orderFacade.processOrder(order, modelCustomer, store, language);
		}

		// save order id in session
		super.setSessionAttribute(Constants.ORDER_ID, modelOrder.getId(), request);
		// set a unique token for confirmation
		super.setSessionAttribute(Constants.ORDER_ID_TOKEN, modelOrder.getId(), request);

		// get cart
		String cartCode = super.getSessionAttribute(Constants.SHOPPING_CART, request);
		if (StringUtils.isNotBlank(cartCode)) {
			try {
				shoppingCartFacade.deleteShoppingCart(cartCode, store);
			} catch (Exception e) {
				log.error("Cannot delete cart " + cartCode, e);
				throw new ServiceException(e);
			}
		}

		// cleanup the order objects
		super.removeAttribute(Constants.ORDER, request);
		super.removeAttribute(Constants.ORDER_SUMMARY, request);
		super.removeAttribute(Constants.INIT_TRANSACTION_KEY, request);
		super.removeAttribute(Constants.SHIPPING_OPTIONS, request);
		super.removeAttribute(Constants.SHIPPING_SUMMARY, request);
		super.removeAttribute(Constants.SHOPPING_CART, request);

		try {
			// refresh customer --
			modelCustomer = customerFacade.getCustomerByUserName(modelCustomer.getNick(), store);

			// if has downloads, authenticate

			// check if any downloads exist for this order6
			List<OrderProductDownload> orderProductDownloads = orderProdctDownloadService
					.getByOrderId(modelOrder.getId());
			if (CollectionUtils.isNotEmpty(orderProductDownloads)) {

				log.debug("Is user authenticated ? ", auth.isAuthenticated());
				if (auth != null && request.isUserInRole("AUTH_CUSTOMER")) {
					// already authenticated
				} else {
					// authenticate
					customerFacade.authenticate(modelCustomer, userName, password);
					super.setSessionAttribute(Constants.CUSTOMER, modelCustomer, request);
				}
				// send new user registration template
				if (order.getCustomer().getId() == null || order.getCustomer().getId().longValue() == 0) {
					// send email for new customer
					customer.setClearPassword(password);// set clear password
														// for email
					customer.setUserName(userName);
					emailTemplatesUtils.sendRegistrationEmail(customer, store, locale, request.getContextPath());
				}
			}

			// send order confirmation email to customer
			emailTemplatesUtils.sendOrderEmail(modelCustomer.getEmailAddress(), modelCustomer, modelOrder, locale,
					language, store, request.getContextPath());

			if (orderService.hasDownloadFiles(modelOrder)) {
				emailTemplatesUtils.sendOrderDownloadEmail(modelCustomer, modelOrder, store, locale,
						request.getContextPath());

			}

			// send order confirmation email to merchant
			emailTemplatesUtils.sendOrderEmail(store.getStoreEmailAddress(), modelCustomer, modelOrder, locale,
					language, store, request.getContextPath());

		} catch (Exception e) {
			log.error("Error while post processing order", e);
		}

		return modelOrder;

	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/commitOrder.html")
	public String commitOrder(@CookieValue("cart") String cookie,
			@Valid @ModelAttribute(value = "order") ShopOrder order, BindingResult bindingResult, Model model,
			HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language) request.getAttribute("LANGUAGE");
		// validate if session has expired

		model.addAttribute("order", order);// TODO remove

		Map<String, Object> configs = (Map<String, Object>) request.getAttribute(Constants.REQUEST_CONFIGS);

		if (configs != null && configs.containsKey(Constants.DEBUG_MODE)) {
			Boolean debugMode = (Boolean) configs.get(Constants.DEBUG_MODE);
			if (debugMode) {
				try {
					ObjectMapper mapper = new ObjectMapper();
					String jsonInString = mapper.writeValueAsString(order);
					log.debug("Commit order -> " + jsonInString);
				} catch (Exception de) {
					log.error(de.getMessage());
				}
			}
		}

		try {

			ShippingMetaData shippingMetaData = shippingService.getShippingMetaData(store);
			model.addAttribute("shippingMetaData", shippingMetaData);
			// basic stuff
			String shoppingCartCode = (String) request.getSession().getAttribute(Constants.SHOPPING_CART);
			if (shoppingCartCode == null) {

				if (cookie == null) {// session expired and cookie null, nothing
										// to do
					StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Pages.timeout)
							.append(".").append(store.getStoreTemplate());
					return template.toString();
				}
				String merchantCookie[] = cookie.split("_");
				String merchantStoreCode = merchantCookie[0];
				if (!merchantStoreCode.equals(store.getCode())) {
					StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Pages.timeout)
							.append(".").append(store.getStoreTemplate());
					return template.toString();
				}
				shoppingCartCode = merchantCookie[1];
			}
			co.hooghly.commerce.domain.ShoppingCart cart = null;

			if (StringUtils.isBlank(shoppingCartCode)) {
				StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Pages.timeout).append(".")
						.append(store.getStoreTemplate());
				return template.toString();
			}
			cart = shoppingCartFacade.getShoppingCartModel(shoppingCartCode, store);

			// readable shopping cart items for order summary box
			ShoppingCartData shoppingCart = shoppingCartFacade.getShoppingCartData(cart);
			model.addAttribute("cart", shoppingCart);

			Set<ShoppingCartItem> items = cart.getLineItems();
			List<ShoppingCartItem> cartItems = new ArrayList<ShoppingCartItem>(items);
			order.setShoppingCartItems(cartItems);

			// get payment methods
			List<PaymentMethod> paymentMethods = paymentService.getAcceptedPaymentMethods(store);
			boolean freeShoppingCart = shoppingCartService.isFreeShoppingCart(cart);

			// not free and no payment methods
			if (CollectionUtils.isEmpty(paymentMethods) && !freeShoppingCart) {
				log.error("No payment method configured");
				model.addAttribute("errorMessages", "No payments configured");
			}

			if (!CollectionUtils.isEmpty(paymentMethods)) {// select default
															// payment method
				PaymentMethod defaultPaymentSelected = null;
				for (PaymentMethod paymentMethod : paymentMethods) {
					if (paymentMethod.isDefaultSelected()) {
						defaultPaymentSelected = paymentMethod;
						break;
					}
				}

				if (defaultPaymentSelected == null) {// forced default selection
					defaultPaymentSelected = paymentMethods.get(0);
					defaultPaymentSelected.setDefaultSelected(true);
				}

			}

			ShippingQuote quote = orderFacade.getShippingQuote(order.getCustomer(), cart, order, store, language);

			if (quote != null) {

				// save quotes in HttpSession
				List<ShippingOption> options = quote.getShippingOptions();
				request.getSession().setAttribute(Constants.SHIPPING_OPTIONS, options);

				if (!CollectionUtils.isEmpty(options)) {

					for (ShippingOption shipOption : options) {

						StringBuilder moduleName = new StringBuilder();
						moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());

						String carrier = messages.getMessage(moduleName.toString(), locale);

						shipOption.setDescription(carrier);

						// option name
						if (!StringUtils.isBlank(shipOption.getOptionCode())) {
							// try to get the translate
							StringBuilder optionCodeBuilder = new StringBuilder();
							try {

								optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode())
										.append(".").append(shipOption.getOptionCode());
								String optionName = messages.getMessage(optionCodeBuilder.toString(), locale);
								shipOption.setOptionName(optionName);
							} catch (Exception e) {// label not found
								log.warn("No shipping code found for " + optionCodeBuilder.toString());
							}
						}

					}

				}

				if (quote.getDeliveryAddress() != null) {
					ReadableCustomerDeliveryAddressPopulator addressPopulator = new ReadableCustomerDeliveryAddressPopulator();
					addressPopulator.setCountryService(countryService);
					addressPopulator.setZoneService(zoneService);
					ReadableDelivery deliveryAddress = new ReadableDelivery();
					addressPopulator.populate(quote.getDeliveryAddress(), deliveryAddress, store, language);
					model.addAttribute("deliveryAddress", deliveryAddress);
				}

			}

			model.addAttribute("shippingQuote", quote);
			model.addAttribute("paymentMethods", paymentMethods);

			if (quote != null) {
				List<Country> shippingCountriesList = orderFacade.getShipToCountry(store, language);
				model.addAttribute("countries", shippingCountriesList);
			} else {
				// get all countries
				List<Country> countries = countryService.getCountries(language);
				model.addAttribute("countries", countries);
			}

			// set shipping summary
			if (order.getSelectedShippingOption() != null) {
				ShippingSummary summary = (ShippingSummary) request.getSession()
						.getAttribute(Constants.SHIPPING_SUMMARY);
				@SuppressWarnings("unchecked")
				List<ShippingOption> options = (List<ShippingOption>) request.getSession()
						.getAttribute(Constants.SHIPPING_OPTIONS);

				if (summary == null) {
					summary = orderFacade.getShippingSummary(quote, store, language);
					request.getSession().setAttribute(Constants.SHIPPING_SUMMARY, options);
				}

				if (options == null) {
					options = quote.getShippingOptions();
					request.getSession().setAttribute(Constants.SHIPPING_OPTIONS, options);
				}

				ReadableShippingSummary readableSummary = new ReadableShippingSummary();
				ReadableShippingSummaryPopulator readableSummaryPopulator = new ReadableShippingSummaryPopulator();
				readableSummaryPopulator.setPricingService(pricingService);
				readableSummaryPopulator.populate(summary, readableSummary, store, language);

				if (!CollectionUtils.isEmpty(options)) {

					// get submitted shipping option
					ShippingOption quoteOption = null;
					ShippingOption selectedOption = order.getSelectedShippingOption();

					// check if selectedOption exist
					for (ShippingOption shipOption : options) {
						if (!StringUtils.isBlank(shipOption.getOptionId())
								&& shipOption.getOptionId().equals(selectedOption.getOptionId())) {
							quoteOption = shipOption;
						}

					}
					if (quoteOption == null) {
						quoteOption = options.get(0);
					}

					readableSummary.setSelectedShippingOption(quoteOption);
					readableSummary.setShippingOptions(options);
					summary.setShippingOption(quoteOption.getOptionId());
					summary.setShipping(quoteOption.getOptionPrice());

				}

				order.setShippingSummary(summary);
			}

			OrderTotalSummary totalSummary = super.getSessionAttribute(Constants.ORDER_SUMMARY, request);

			if (totalSummary == null) {
				totalSummary = orderFacade.calculateOrderTotal(store, order, language);
				super.setSessionAttribute(Constants.ORDER_SUMMARY, totalSummary, request);
			}

			order.setOrderTotalSummary(totalSummary);

			orderFacade.validateOrder(order, bindingResult, new HashMap<String, String>(), store, locale);

			if (bindingResult.hasErrors()) {
				log.info("found {} validation error while validating in customer registration ",
						bindingResult.getErrorCount());
				StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Checkout.checkout)
						.append(".").append(store.getStoreTemplate());
				return template.toString();

			}

			@SuppressWarnings("unused")
			Order modelOrder = this.commitOrder(order, request, locale);

		} catch (ServiceException se) {

			log.error("Error while creating an order ", se);

			String defaultMessage = messages.getMessage("message.error", locale);
			model.addAttribute("errorMessages", defaultMessage);

			if (se.getExceptionType() == ServiceException.EXCEPTION_VALIDATION) {
				if (!StringUtils.isBlank(se.getMessageCode())) {
					String messageLabel = messages.getMessage(se.getMessageCode(), locale, defaultMessage);
					model.addAttribute("errorMessages", messageLabel);
				}
			} else if (se.getExceptionType() == ServiceException.EXCEPTION_PAYMENT_DECLINED) {
				String paymentDeclinedMessage = messages.getMessage("message.payment.declined", locale);
				if (!StringUtils.isBlank(se.getMessageCode())) {
					String messageLabel = messages.getMessage(se.getMessageCode(), locale, paymentDeclinedMessage);
					model.addAttribute("errorMessages", messageLabel);
				} else {
					model.addAttribute("errorMessages", paymentDeclinedMessage);
				}
			}

			StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Checkout.checkout).append(".")
					.append(store.getStoreTemplate());
			return template.toString();

		} catch (Exception e) {
			log.error("Error while commiting order", e);
			throw e;

		}

		// redirect to completd
		return "redirect:/shop/order/confirmation.html";

	}

	/**
	 * Recalculates shipping and tax following a change in country or province
	 * 
	 * @param order
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/shippingQuotes.json" }, method = RequestMethod.POST)
	public @ResponseBody ReadableShopOrder calculateShipping(@ModelAttribute(value = "order") ShopOrder order,
			HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		Language language = (Language) request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
		String shoppingCartCode = getSessionAttribute(Constants.SHOPPING_CART, request);

		Map<String, Object> configs = (Map<String, Object>) request.getAttribute(Constants.REQUEST_CONFIGS);

		/*
		 * if(configs!=null && configs.containsKey(Constants.DEBUG_MODE)) {
		 * Boolean debugMode = (Boolean) configs.get(Constants.DEBUG_MODE);
		 * if(debugMode) { try { ObjectMapper mapper = new ObjectMapper();
		 * String jsonInString = mapper.writeValueAsString(order);
		 * log.info("Calculate order -> shoppingCartCode[ " + shoppingCartCode +
		 * "] -> " + jsonInString); } catch(Exception de) {
		 * log.error(de.getMessage()); } } }
		 */

		Validate.notNull(shoppingCartCode, "shoppingCartCode does not exist in the session");

		ReadableShopOrder readableOrder = new ReadableShopOrder();
		try {

			// re-generate cart
			co.hooghly.commerce.domain.ShoppingCart cart = shoppingCartFacade
					.getShoppingCartModel(shoppingCartCode, store);

			ReadableShopOrderPopulator populator = new ReadableShopOrderPopulator();
			populator.populate(order, readableOrder, store, language);

			boolean requiresShipping = shoppingCartService.requiresShipping(cart);

			/** shipping **/
			ShippingQuote quote = null;
			if (requiresShipping) {
				quote = orderFacade.getShippingQuote(order.getCustomer(), cart, order, store, language);
			}

			if (quote != null) {
				String shippingReturnCode = quote.getShippingReturnCode();
				if (CollectionUtils.isNotEmpty(quote.getShippingOptions())
						|| ShippingQuote.NO_POSTAL_CODE.equals(shippingReturnCode)) {

					ShippingSummary summary = orderFacade.getShippingSummary(quote, store, language);
					order.setShippingSummary(summary);// for total calculation

					ReadableShippingSummary readableSummary = new ReadableShippingSummary();
					ReadableShippingSummaryPopulator readableSummaryPopulator = new ReadableShippingSummaryPopulator();
					readableSummaryPopulator.setPricingService(pricingService);
					readableSummaryPopulator.populate(summary, readableSummary, store, language);

					// additional informations
					/*
					 * if(quote.getQuoteInformations() != null &&
					 * quote.getQuoteInformations().size() >0) { for(String k :
					 * quote.getQuoteInformations().keySet()) { Object o =
					 * quote.getQuoteInformations().get(k); try {
					 * readableSummary.getQuoteInformations().put(k,
					 * String.valueOf(o)); } catch(Exception e) {
					 * log.error("Cannot cast value to string " +
					 * e.getMessage()); } } }
					 */

					if (quote.getDeliveryAddress() != null) {
						ReadableCustomerDeliveryAddressPopulator addressPopulator = new ReadableCustomerDeliveryAddressPopulator();
						addressPopulator.setCountryService(countryService);
						addressPopulator.setZoneService(zoneService);
						ReadableDelivery deliveryAddress = new ReadableDelivery();
						addressPopulator.populate(quote.getDeliveryAddress(), deliveryAddress, store, language);
						// model.addAttribute("deliveryAddress",
						// deliveryAddress);
						readableOrder.setDelivery(deliveryAddress);
						super.setSessionAttribute(Constants.KEY_SESSION_ADDRESS, deliveryAddress, request);
					}

					// save quotes in HttpSession
					List<ShippingOption> options = quote.getShippingOptions();

					if (!CollectionUtils.isEmpty(options)) {

						for (ShippingOption shipOption : options) {

							StringBuilder moduleName = new StringBuilder();
							moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());

							String carrier = messages.getMessage(moduleName.toString(),
									new String[] { store.getStorename() }, locale);

							String note = messages.getMessage(moduleName.append(".note").toString(), locale, "");

							shipOption.setDescription(carrier);
							shipOption.setNote(note);

							// option name
							if (!StringUtils.isBlank(shipOption.getOptionCode())) {
								// try to get the translate
								StringBuilder optionCodeBuilder = new StringBuilder();
								try {

									optionCodeBuilder.append("module.shipping.")
											.append(shipOption.getShippingModuleCode());
									String optionName = messages.getMessage(optionCodeBuilder.toString(), locale);
									shipOption.setOptionName(optionName);
								} catch (Exception e) {// label not found
									log.warn("No shipping code found for " + optionCodeBuilder.toString());
								}
							}

						}

					}

					readableSummary.setSelectedShippingOption(quote.getSelectedShippingOption());

					readableSummary.setShippingOptions(options);

					readableOrder.setShippingSummary(readableSummary);// TODO
																		// add
																		// readable
																		// address
					request.getSession().setAttribute(Constants.SHIPPING_SUMMARY, summary);
					request.getSession().setAttribute(Constants.SHIPPING_OPTIONS, options);
					request.getSession().setAttribute("SHIPPING_INFORMATIONS", readableSummary.getQuoteInformations());

					if (configs != null && configs.containsKey(Constants.DEBUG_MODE)) {
						Boolean debugMode = (Boolean) configs.get(Constants.DEBUG_MODE);
						if (debugMode) {

							try {
								ObjectMapper mapper = new ObjectMapper();
								String jsonInString = mapper.writeValueAsString(readableOrder);
								log.debug("Readable order -> shoppingCartCode[ " + shoppingCartCode + "] -> "
										+ jsonInString);
								System.out.println("Readable order -> shoppingCartCode[ " + shoppingCartCode + "] -> "
										+ jsonInString);
							} catch (Exception de) {
								log.error(de.getMessage());
							}

						}
					}

				}

				if (quote.getShippingReturnCode() != null
						&& quote.getShippingReturnCode().equals(ShippingQuote.NO_SHIPPING_MODULE_CONFIGURED)) {
					log.error("Shipping quote error " + quote.getShippingReturnCode());
					readableOrder.setErrorMessage(messages.getMessage("message.noshipping", locale));
				}

				if (quote.getShippingReturnCode() != null
						&& quote.getShippingReturnCode().equals(ShippingQuote.NO_SHIPPING_TO_SELECTED_COUNTRY)) {
					if (CollectionUtils.isEmpty(quote.getShippingOptions())) {// only
																				// if
																				// there
																				// are
																				// no
																				// other
																				// options
						log.error("Shipping quote error " + quote.getShippingReturnCode());
						readableOrder.setErrorMessage(messages.getMessage("message.noshipping", locale));
					}
				}

				// if(quote.getShippingReturnCode()!=null &&
				// quote.getShippingReturnCode().equals(ShippingQuote.NO_POSTAL_CODE))
				// {
				// log.error("Shipping quote error " +
				// quote.getShippingReturnCode());
				// readableOrder.setErrorMessage(messages.getMessage("message.noshipping",
				// locale));
				// }

				if (!StringUtils.isBlank(quote.getQuoteError())) {
					log.error("Shipping quote error " + quote.getQuoteError());
					readableOrder.setErrorMessage(messages.getMessage("message.noshippingerror", locale));
				}

			}

			// set list of shopping cart items for core price calculation
			List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(cart.getLineItems());
			order.setShoppingCartItems(items);

			OrderTotalSummary orderTotalSummary = orderFacade.calculateOrderTotal(store, order, language);
			super.setSessionAttribute(Constants.ORDER_SUMMARY, orderTotalSummary, request);

			ReadableOrderTotalPopulator totalPopulator = new ReadableOrderTotalPopulator();
			totalPopulator.setMessages(messages);
			totalPopulator.setPricingService(pricingService);

			List<ReadableOrderTotal> subtotals = new ArrayList<ReadableOrderTotal>();
			for (OrderTotal total : orderTotalSummary.getTotals()) {
				if (!total.getOrderTotalCode().equals("order.total.total")) {
					ReadableOrderTotal t = new ReadableOrderTotal();
					totalPopulator.populate(total, t, store, language);
					subtotals.add(t);
				} else {// grand total
					ReadableOrderTotal ot = new ReadableOrderTotal();
					totalPopulator.populate(total, ot, store, language);
					readableOrder.setGrandTotal(ot.getTotal());
				}
			}

			readableOrder.setSubTotals(subtotals);

		} catch (Exception e) {
			log.error("Error while getting shipping quotes", e);
			readableOrder.setErrorMessage(messages.getMessage("message.error", locale));
		}

		return readableOrder;
	}

	/**
	 * Calculates the order total following price variation like changing a
	 * shipping option
	 * 
	 * @param order
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = { "/calculateOrderTotal.json" }, method = RequestMethod.POST)
	public @ResponseBody ReadableShopOrder calculateOrderTotal(@ModelAttribute(value = "order") ShopOrder order,
			HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {

		Language language = (Language) request.getAttribute("LANGUAGE");
		MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
		String shoppingCartCode = getSessionAttribute(Constants.SHOPPING_CART, request);

		Validate.notNull(shoppingCartCode, "shoppingCartCode does not exist in the session");

		ReadableShopOrder readableOrder = new ReadableShopOrder();
		try {

			// re-generate cart
			co.hooghly.commerce.domain.ShoppingCart cart = shoppingCartFacade
					.getShoppingCartModel(shoppingCartCode, store);

			ReadableShopOrderPopulator populator = new ReadableShopOrderPopulator();
			populator.populate(order, readableOrder, store, language);

			ReadableDelivery readableDelivery = super.getSessionAttribute(Constants.KEY_SESSION_ADDRESS, request);

			if (order.getSelectedShippingOption() != null) {
				ShippingSummary summary = (ShippingSummary) request.getSession()
						.getAttribute(Constants.SHIPPING_SUMMARY);
				@SuppressWarnings("unchecked")
				List<ShippingOption> options = (List<ShippingOption>) request.getSession()
						.getAttribute(Constants.SHIPPING_OPTIONS);

				order.setShippingSummary(summary);// for total calculation

				ReadableShippingSummary readableSummary = new ReadableShippingSummary();
				ReadableShippingSummaryPopulator readableSummaryPopulator = new ReadableShippingSummaryPopulator();
				readableSummaryPopulator.setPricingService(pricingService);
				readableSummaryPopulator.populate(summary, readableSummary, store, language);

				// override summary
				readableSummary.setDelivery(readableDelivery);

				if (!CollectionUtils.isEmpty(options)) {

					// get submitted shipping option
					ShippingOption quoteOption = null;
					ShippingOption selectedOption = order.getSelectedShippingOption();

					// check if selectedOption exist
					for (ShippingOption shipOption : options) {

						StringBuilder moduleName = new StringBuilder();
						moduleName.append("module.shipping.").append(shipOption.getShippingModuleCode());

						String carrier = messages.getMessage(moduleName.toString(), locale);
						String note = messages.getMessage(moduleName.append(".note").toString(), locale, "");

						shipOption.setNote(note);

						shipOption.setDescription(carrier);
						if (!StringUtils.isBlank(shipOption.getOptionId())
								&& shipOption.getOptionId().equals(selectedOption.getOptionId())) {
							quoteOption = shipOption;
						}

						// option name
						if (!StringUtils.isBlank(shipOption.getOptionCode())) {
							// try to get the translate
							StringBuilder optionCodeBuilder = new StringBuilder();
							try {

								// optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode()).append(".").append(shipOption.getOptionCode());
								optionCodeBuilder.append("module.shipping.").append(shipOption.getShippingModuleCode());
								String optionName = messages.getMessage(optionCodeBuilder.toString(), locale);
								shipOption.setOptionName(optionName);
							} catch (Exception e) {// label not found
								log.warn("No shipping code found for " + optionCodeBuilder.toString());
							}
						}
					}

					if (quoteOption == null) {
						quoteOption = options.get(0);
					}

					readableSummary.setSelectedShippingOption(quoteOption);
					readableSummary.setShippingOptions(options);

					summary.setShippingOption(quoteOption.getOptionId());
					summary.setShippingOptionCode(quoteOption.getOptionCode());
					summary.setShipping(quoteOption.getOptionPrice());
					order.setShippingSummary(summary);// override with new
														// summary

					@SuppressWarnings("unchecked")
					Map<String, String> informations = (Map<String, String>) request.getSession()
							.getAttribute("SHIPPING_INFORMATIONS");
					readableSummary.setQuoteInformations(informations);

				}

				readableOrder.setShippingSummary(readableSummary);// TODO
																	// readable
																	// address
																	// format
				readableOrder.setDelivery(readableDelivery);
			}

			// set list of shopping cart items for core price calculation
			List<ShoppingCartItem> items = new ArrayList<ShoppingCartItem>(cart.getLineItems());
			order.setShoppingCartItems(items);

			// order total calculation
			OrderTotalSummary orderTotalSummary = orderFacade.calculateOrderTotal(store, order, language);
			super.setSessionAttribute(Constants.ORDER_SUMMARY, orderTotalSummary, request);

			ReadableOrderTotalPopulator totalPopulator = new ReadableOrderTotalPopulator();
			totalPopulator.setMessages(messages);
			totalPopulator.setPricingService(pricingService);

			List<ReadableOrderTotal> subtotals = new ArrayList<ReadableOrderTotal>();
			for (OrderTotal total : orderTotalSummary.getTotals()) {
				if (total.getOrderTotalCode() == null || !total.getOrderTotalCode().equals("order.total.total")) {
					ReadableOrderTotal t = new ReadableOrderTotal();
					totalPopulator.populate(total, t, store, language);
					subtotals.add(t);
				} else {// grand total
					ReadableOrderTotal ot = new ReadableOrderTotal();
					totalPopulator.populate(total, ot, store, language);
					readableOrder.setGrandTotal(ot.getTotal());
				}
			}

			readableOrder.setSubTotals(subtotals);

		} catch (Exception e) {
			log.error("Error while getting shipping quotes", e);
			readableOrder.setErrorMessage(messages.getMessage("message.error", locale));
		}

		return readableOrder;
	}

}