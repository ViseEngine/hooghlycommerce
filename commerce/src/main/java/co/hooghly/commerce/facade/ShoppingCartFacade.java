/**
 *
 */
package co.hooghly.commerce.facade;

import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.ProductAttributeService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.business.ServiceException;
import co.hooghly.commerce.business.ShoppingCartCalculationService;
import co.hooghly.commerce.business.ShoppingCartService;
import co.hooghly.commerce.business.utils.ProductPriceUtils;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.FinalPrice;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAttribute;
import co.hooghly.commerce.domain.ShoppingCart;
import co.hooghly.commerce.util.ImageFilePath;
import co.hooghly.commerce.web.populator.ShoppingCartDataPopulator;
import co.hooghly.commerce.web.ui.CartModificationException;
import co.hooghly.commerce.web.ui.ShoppingCartAttribute;
import co.hooghly.commerce.web.ui.ShoppingCartData;
import co.hooghly.commerce.web.ui.ShoppingCartItem;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.inject.Inject;
import javax.persistence.NoResultException;
import java.util.*;

@Service
@Slf4j
public class ShoppingCartFacade  {

	@Inject
	private ShoppingCartService shoppingCartService;

	@Inject
	ShoppingCartCalculationService shoppingCartCalculationService;

	@Inject
	private ProductPriceUtils productPriceUtils;

	@Inject
	private ProductService productService;

	@Inject
	private PricingService pricingService;

	@Inject
	private ProductAttributeService productAttributeService;

	//@Inject
	//@Qualifier("img")
	private ImageFilePath imageUtils;

	public void deleteShoppingCart(final Long id, final MerchantStore store) throws Exception {
		ShoppingCart cart = shoppingCartService.getById(id, store);
		if (cart != null) {
			shoppingCartService.deleteCart(cart);
		}
	}

	
	public void deleteShoppingCart(final String code, final MerchantStore store) throws Exception {
		ShoppingCart cart = shoppingCartService.getByCode(code, store);
		if (cart != null) {
			shoppingCartService.deleteCart(cart);
		}
	}

	
	public ShoppingCartData addItemsToShoppingCart(final ShoppingCartData shoppingCartData, final ShoppingCartItem item,
			final MerchantStore store, final Language language, final Customer customer) throws Exception {

		ShoppingCart cartModel = null;
		if (!StringUtils.isBlank(item.getCode())) {
			// get it from the db
			cartModel = getShoppingCartModel(item.getCode(), store);
			if (cartModel == null) {
				cartModel = createCartModel(shoppingCartData.getCode(), store, customer);
			}

		}

		if (cartModel == null) {

			final String shoppingCartCode = StringUtils.isNotBlank(shoppingCartData.getCode())
					? shoppingCartData.getCode() : null;
			cartModel = createCartModel(shoppingCartCode, store, customer);

		}
		co.hooghly.commerce.domain.ShoppingCartItem shoppingCartItem = createCartItem(cartModel, item,
				store);

		boolean duplicateFound = false;
		if (CollectionUtils.isEmpty(item.getShoppingCartAttributes())) {// increment
																		// quantity
			// get duplicate item from the cart
			Set<co.hooghly.commerce.domain.ShoppingCartItem> cartModelItems = cartModel.getLineItems();
			for (co.hooghly.commerce.domain.ShoppingCartItem cartItem : cartModelItems) {
				if (cartItem.getProduct().getId().longValue() == shoppingCartItem.getProduct().getId().longValue()) {
					if (CollectionUtils.isEmpty(cartItem.getAttributes())) {
						if (!duplicateFound) {
							if (!shoppingCartItem.isProductVirtual()) {
								cartItem.setQuantity(cartItem.getQuantity() + shoppingCartItem.getQuantity());
							}
							duplicateFound = true;
							break;
						}
					}
				}
			}
		}

		if (!duplicateFound) {
			cartModel.getLineItems().add(shoppingCartItem);
		}

		/** Update cart in database with line items **/
		shoppingCartService.saveOrUpdate(cartModel);

		// refresh cart
		cartModel = shoppingCartService.getById(cartModel.getId(), store);

		shoppingCartCalculationService.calculate(cartModel, store, language);

		ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
		shoppingCartDataPopulator.setShoppingCartCalculationService(shoppingCartCalculationService);
		shoppingCartDataPopulator.setPricingService(pricingService);
		shoppingCartDataPopulator.setimageUtils(imageUtils);

		return shoppingCartDataPopulator.populate(cartModel, store, language);
	}

	private co.hooghly.commerce.domain.ShoppingCartItem createCartItem(final ShoppingCart cartModel,
			final ShoppingCartItem shoppingCartItem, final MerchantStore store) throws Exception {

		Product product = productService.findOne(shoppingCartItem.getProductId());

		if (product == null) {
			throw new Exception("Item with id " + shoppingCartItem.getProductId() + " does not exist");
		}

		if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			throw new Exception("Item with id " + shoppingCartItem.getProductId() + " does not belong to merchant "
					+ store.getId());
		}

		co.hooghly.commerce.domain.ShoppingCartItem item = shoppingCartService
				.populateShoppingCartItem(product);

		item.setQuantity(shoppingCartItem.getQuantity());
		item.setShoppingCart(cartModel);

		// attributes
		List<ShoppingCartAttribute> cartAttributes = shoppingCartItem.getShoppingCartAttributes();
		if (!CollectionUtils.isEmpty(cartAttributes)) {
			for (ShoppingCartAttribute attribute : cartAttributes) {
				ProductAttribute productAttribute = productAttributeService.getById(attribute.getAttributeId());
				if (productAttribute != null
						&& productAttribute.getProduct().getId().longValue() == product.getId().longValue()) {
					co.hooghly.commerce.domain.ShoppingCartAttributeItem attributeItem = new co.hooghly.commerce.domain.ShoppingCartAttributeItem(
							item, productAttribute);

					item.addAttributes(attributeItem);
				}
			}
		}
		return item;

	}

	
	public ShoppingCart createCartModel(final String shoppingCartCode, final MerchantStore store,
			final Customer customer) throws Exception {
		final Long CustomerId = customer != null ? customer.getId() : null;
		ShoppingCart cartModel = new ShoppingCart();
		if (StringUtils.isNotBlank(shoppingCartCode)) {
			cartModel.setShoppingCartCode(shoppingCartCode);
		} else {
			cartModel.setShoppingCartCode(UUID.randomUUID().toString().replaceAll("-", ""));
		}

		cartModel.setMerchantStore(store);
		if (CustomerId != null) {
			cartModel.setCustomerId(CustomerId);
		}
		shoppingCartService.create(cartModel);
		return cartModel;
	}

	private co.hooghly.commerce.domain.ShoppingCartItem getEntryToUpdate(final long entryId,
			final ShoppingCart cartModel) {
		if (CollectionUtils.isNotEmpty(cartModel.getLineItems())) {
			for (co.hooghly.commerce.domain.ShoppingCartItem shoppingCartItem : cartModel
					.getLineItems()) {
				if (shoppingCartItem.getId().longValue() == entryId) {
					log.info("Found line item  for given entry id: " + entryId);
					return shoppingCartItem;

				}
			}
		}
		log.info("Unable to find any entry for given Id: " + entryId);
		return null;
	}

	private Object getKeyValue(final String key) {
		ServletRequestAttributes reqAttr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		return reqAttr.getRequest().getAttribute(key);
	}

	
	public ShoppingCartData getShoppingCartData(final Customer customer, final MerchantStore store,
			final String shoppingCartId) throws Exception {

		ShoppingCart cart = null;
		log.info("Reteriving customer shopping cart...{} | {} | {}", customer, store.getId(), shoppingCartId);
		try {
			if (customer != null) {
				log.info("Reteriving customer shopping cart...");

				cart = shoppingCartService.getShoppingCart(customer);

			}

			else {
				if (StringUtils.isNotBlank(shoppingCartId)) {
					log.info("Get shopping cart by id");
					cart = shoppingCartService.getByCode(shoppingCartId, store);
				}

			}
		} catch (Exception ex) {
			log.error("Error while retriving cart from customer", ex);
		} 

		if (cart == null) {
			return null;
		}

		log.info("Cart model found.");

		ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
		shoppingCartDataPopulator.setShoppingCartCalculationService(shoppingCartCalculationService);
		shoppingCartDataPopulator.setPricingService(pricingService);
		shoppingCartDataPopulator.setimageUtils(imageUtils);

		Language language = (Language) getKeyValue(Constants.LANGUAGE);
		MerchantStore merchantStore = (MerchantStore) getKeyValue(Constants.MERCHANT_STORE);

		ShoppingCartData shoppingCartData = shoppingCartDataPopulator.populate(cart, merchantStore, language);

		return shoppingCartData;

	}

	
	public ShoppingCartData getShoppingCartData(final ShoppingCart shoppingCartModel) throws Exception {

		ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
		shoppingCartDataPopulator.setShoppingCartCalculationService(shoppingCartCalculationService);
		shoppingCartDataPopulator.setPricingService(pricingService);
		shoppingCartDataPopulator.setimageUtils(imageUtils);
		Language language = (Language) getKeyValue(Constants.LANGUAGE);
		MerchantStore merchantStore = (MerchantStore) getKeyValue(Constants.MERCHANT_STORE);
		return shoppingCartDataPopulator.populate(shoppingCartModel, merchantStore, language);
	}

	
	public ShoppingCartData removeCartItem(final Long itemID, final String cartId, final MerchantStore store,
			final Language language) throws Exception {
		if (StringUtils.isNotBlank(cartId)) {

			ShoppingCart cartModel = getCartModel(cartId, store);
			if (cartModel != null) {
				if (CollectionUtils.isNotEmpty(cartModel.getLineItems())) {
					Set<co.hooghly.commerce.domain.ShoppingCartItem> shoppingCartItemSet = new HashSet<co.hooghly.commerce.domain.ShoppingCartItem>();
					for (co.hooghly.commerce.domain.ShoppingCartItem shoppingCartItem : cartModel
							.getLineItems()) {
						// if ( shoppingCartItem.getId().longValue() !=
						// itemID.longValue() )
						if (shoppingCartItem.getId().longValue() == itemID.longValue()) {
							// shoppingCartItemSet.add( shoppingCartItem );
							shoppingCartService.deleteShoppingCartItem(itemID);
						}
					}
					// cartModel.setLineItems( shoppingCartItemSet );
					// shoppingCartService.saveOrUpdate( cartModel );

					cartModel = getCartModel(cartId, store);

					ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
					shoppingCartDataPopulator.setShoppingCartCalculationService(shoppingCartCalculationService);
					shoppingCartDataPopulator.setPricingService(pricingService);
					shoppingCartDataPopulator.setimageUtils(imageUtils);
					return shoppingCartDataPopulator.populate(cartModel, store, language);
				}
			}
		}
		return null;
	}

	
	public ShoppingCartData updateCartItem(final Long itemID, final String cartId, final long newQuantity,
			final MerchantStore store, final Language language) throws Exception {
		if (newQuantity < 1) {
			throw new CartModificationException("Quantity must not be less than one");
		}
		if (StringUtils.isNotBlank(cartId)) {
			ShoppingCart cartModel = getCartModel(cartId, store);
			if (cartModel != null) {
				co.hooghly.commerce.domain.ShoppingCartItem entryToUpdate = getEntryToUpdate(
						itemID.longValue(), cartModel);

				if (entryToUpdate == null) {
					throw new CartModificationException("Unknown entry number.");
				}

				entryToUpdate.getProduct();

				log.info("Updating cart entry quantity to" + newQuantity);
				entryToUpdate.setQuantity((int) newQuantity);
				List<ProductAttribute> productAttributes = new ArrayList<ProductAttribute>();
				productAttributes.addAll(entryToUpdate.getProduct().getAttributes());
				final FinalPrice finalPrice = productPriceUtils.getFinalProductPrice(entryToUpdate.getProduct(),
						productAttributes);
				entryToUpdate.setItemPrice(finalPrice.getFinalPrice());
				shoppingCartService.saveOrUpdate(cartModel);

				log.info("Cart entry updated with desired quantity");
				ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
				shoppingCartDataPopulator.setShoppingCartCalculationService(shoppingCartCalculationService);
				shoppingCartDataPopulator.setPricingService(pricingService);
				shoppingCartDataPopulator.setimageUtils(imageUtils);
				return shoppingCartDataPopulator.populate(cartModel, store, language);

			}
		}
		return null;
	}

	
	public ShoppingCartData updateCartItems(final List<ShoppingCartItem> shoppingCartItems, final MerchantStore store,
			final Language language) throws Exception {

		Validate.notEmpty(shoppingCartItems, "shoppingCartItems null or empty");
		ShoppingCart cartModel = null;
		Set<co.hooghly.commerce.domain.ShoppingCartItem> cartItems = new HashSet<co.hooghly.commerce.domain.ShoppingCartItem>();
		for (ShoppingCartItem item : shoppingCartItems) {

			if (item.getQuantity() < 1) {
				throw new CartModificationException("Quantity must not be less than one");
			}

			if (cartModel == null) {
				cartModel = getCartModel(item.getCode(), store);
			}

			co.hooghly.commerce.domain.ShoppingCartItem entryToUpdate = getEntryToUpdate(item.getId(),
					cartModel);

			if (entryToUpdate == null) {
				throw new CartModificationException("Unknown entry number.");
			}

			entryToUpdate.getProduct();

			log.info("Updating cart entry quantity to" + item.getQuantity());
			entryToUpdate.setQuantity((int) item.getQuantity());

			List<ProductAttribute> productAttributes = new ArrayList<ProductAttribute>();
			productAttributes.addAll(entryToUpdate.getProduct().getAttributes());

			final FinalPrice finalPrice = productPriceUtils.getFinalProductPrice(entryToUpdate.getProduct(),
					productAttributes);
			entryToUpdate.setItemPrice(finalPrice.getFinalPrice());

			cartItems.add(entryToUpdate);

		}

		cartModel.setLineItems(cartItems);
		shoppingCartService.saveOrUpdate(cartModel);
		log.info("Cart entry updated with desired quantity");
		ShoppingCartDataPopulator shoppingCartDataPopulator = new ShoppingCartDataPopulator();
		shoppingCartDataPopulator.setShoppingCartCalculationService(shoppingCartCalculationService);
		shoppingCartDataPopulator.setPricingService(pricingService);
		shoppingCartDataPopulator.setimageUtils(imageUtils);
		return shoppingCartDataPopulator.populate(cartModel, store, language);

	}

	private ShoppingCart getCartModel(final String cartId, final MerchantStore store) {
		if (StringUtils.isNotBlank(cartId)) {
			try {
				return shoppingCartService.getByCode(cartId, store);
			} catch (ServiceException e) {
				log.error("unable to find any cart asscoiated with this Id: " + cartId);
				log.error("error while fetching cart model...", e);
				return null;
			} catch (NoResultException nre) {
				// nothing
			}

		}
		return null;
	}

	
	public ShoppingCartData getShoppingCartData(String code, MerchantStore store) {
		try {
			ShoppingCart cartModel = shoppingCartService.getByCode(code, store);
			if (cartModel != null) {
				ShoppingCartData cart = getShoppingCartData(cartModel);
				return cart;
			}
		} catch (NoResultException nre) {
			// nothing

		} catch (Exception e) {
			log.error("Cannot retrieve cart code " + code, e);
		}

		return null;
	}

	@Deprecated
	public ShoppingCart getShoppingCartModel(String shoppingCartCode, MerchantStore store) throws Exception {
		return shoppingCartService.getByCode(shoppingCartCode, store);
	}

	@Deprecated
	public ShoppingCart getShoppingCartModel(Customer customer, MerchantStore store) throws Exception {
		return shoppingCartService.getByCustomer(customer);
	}

	@Deprecated
	public void saveOrUpdateShoppingCart(ShoppingCart cart) throws Exception {
		shoppingCartService.saveOrUpdate(cart);

	}

}
