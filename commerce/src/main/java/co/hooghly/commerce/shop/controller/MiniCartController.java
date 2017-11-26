/**
 * 
 */
package co.hooghly.commerce.shop.controller;

import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.facade.ShoppingCartFacade;
import co.hooghly.commerce.web.ui.ShoppingCartData;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@Controller
@RequestMapping("/shop/cart")
public class MiniCartController extends AbstractController {

	@Inject
	private ShoppingCartFacade shoppingCartFacade;

	@GetMapping("/mini/{shoppingCartCode}")
	public @ResponseBody ShoppingCartData displayMiniCart(@PathVariable String shoppingCartCode, HttpServletRequest request,
			Model model) {
		log.info("--- display mini cart ---" + shoppingCartCode);
		try {
			MerchantStore merchantStore = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
			Customer customer = getSessionAttribute(Constants.CUSTOMER, request);
			ShoppingCartData cart = shoppingCartFacade.getShoppingCartData(customer, merchantStore, shoppingCartCode);
			
			log.info("cart - " + cart);
			
			if (cart != null) {
				request.getSession().setAttribute(Constants.SHOPPING_CART, cart.getCode());
			} else {
				log.info(" === create empty cart === ");
				request.getSession().removeAttribute(Constants.SHOPPING_CART);
				cart = new ShoppingCartData();// create an empty cart
				log.info(" === cart === " + cart);
				cart.setTotal("GBP0.00");
			}
			
			log.info("--- cart getTotal ---" + cart.getTotal());
			log.info("--- cart getQuantity ---" + cart.getQuantity());
			log.info("--- returning cart - " + cart);
			return cart;

		} catch (Exception e) {
			log.error("Error while getting the shopping cart", e);
		}
		log.info("--- returning null ---" );
		return null;

	}

	@RequestMapping(value = { "/removeMiniShoppingCartItem" }, method = { RequestMethod.GET, RequestMethod.POST })
	public @ResponseBody ShoppingCartData removeShoppingCartItem(Long lineItemId, final String shoppingCartCode,
			HttpServletRequest request, Model model) throws Exception {
		Language language = (Language) request.getAttribute(Constants.LANGUAGE);
		MerchantStore merchantStore = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
		ShoppingCartData cart = shoppingCartFacade.getShoppingCartData(null, merchantStore, shoppingCartCode);
		if (cart == null) {
			return null;
		}
		ShoppingCartData shoppingCartData = shoppingCartFacade.removeCartItem(lineItemId, cart.getCode(), merchantStore,
				language);

		if (CollectionUtils.isEmpty(shoppingCartData.getShoppingCartItems())) {
			shoppingCartFacade.deleteShoppingCart(shoppingCartData.getId(), merchantStore);
			request.getSession().removeAttribute(Constants.SHOPPING_CART);
			return null;
		}

		request.getSession().setAttribute(Constants.SHOPPING_CART, cart.getCode());

		log.debug("removed item" + lineItemId + "from cart");
		return shoppingCartData;
	}

}
