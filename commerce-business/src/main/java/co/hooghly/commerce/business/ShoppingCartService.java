package co.hooghly.commerce.business;

import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.FinalPrice;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAttribute;
import co.hooghly.commerce.domain.ShippingProduct;
import co.hooghly.commerce.domain.ShoppingCart;
import co.hooghly.commerce.domain.ShoppingCartAttributeItem;
import co.hooghly.commerce.domain.ShoppingCartItem;
import co.hooghly.commerce.repository.ShoppingCartAttributeRepository;
import co.hooghly.commerce.repository.ShoppingCartItemRepository;
import co.hooghly.commerce.repository.ShoppingCartRepository;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
public class ShoppingCartService extends SalesManagerEntityServiceImpl<Long, ShoppingCart> {

	private ShoppingCartRepository shoppingCartRepository;

	@Autowired
	private ProductService productService;

	@Autowired
	private ShoppingCartItemRepository shoppingCartItemRepository;

	@Autowired
	private ShoppingCartAttributeRepository shoppingCartAttributeItemRepository;

	@Autowired
	private PricingService pricingService;

	@Autowired
	private ProductAttributeService productAttributeService;

	public ShoppingCartService(ShoppingCartRepository shoppingCartRepository) {
		super(shoppingCartRepository);
		this.shoppingCartRepository = shoppingCartRepository;

	}
	
	public ShoppingCart findCartForCheckout(String cookie, String shoppingCartCode, MerchantStore store, Customer customer) {
		ShoppingCart cart = null;

		if (StringUtils.isBlank(shoppingCartCode)) {

			if (cookie == null) {
				// session expired and cookie null, nothing to do
				return cart;
			}
			String merchantCookie[] = cookie.split("_");
			String merchantStoreCode = merchantCookie[0];
			if (!merchantStoreCode.equals(store.getCode())) {
				return cart;
			}
			shoppingCartCode = merchantCookie[1];

		}

		cart = getByCode(shoppingCartCode, store);
		//cart not linked and found in db, lets try with customer info
		if (cart == null && customer != null) {
			cart = getByCustomer(customer);
		}
		
		if (CollectionUtils.isEmpty(cart.getLineItems())) {
			return null;
		}
		
		boolean allAvailables = true;
		// Filter items, delete unavailable
		Set<ShoppingCartItem> availables = new HashSet<ShoppingCartItem>();
		// Take out items no more available
		
		for (ShoppingCartItem item : cart.getLineItems()) {

			Product p = productService.findOne(item.getProduct().getId());
			if (p.isAvailable()) {
				availables.add(item);
			} else {
				allAvailables = false;
			}
		}
		cart.setLineItems(availables);

		if (!allAvailables) {
			saveOrUpdate(cart);
		}
		
		return cart;
	}
	
	
	/**
	 * Retrieve a {@link ShoppingCart} cart for a given customer
	 */
	//TODO - Rename to findByCustomer()
	@Transactional
	public ShoppingCart getShoppingCart(final Customer customer) {

		ShoppingCart shoppingCart = shoppingCartRepository.findByCustomer(customer.getId());
		getPopulatedShoppingCart(shoppingCart);
		if (shoppingCart != null && shoppingCart.isObsolete()) {
			delete(shoppingCart);
			shoppingCart = null;
		}
		return shoppingCart;

	}

	/**
	 * Save or update a {@link ShoppingCart} for a given customer
	 */

	public void saveOrUpdate(final ShoppingCart shoppingCart) throws ServiceException {
		if (shoppingCart.getId() == null || shoppingCart.getId().longValue() == 0) {
			super.create(shoppingCart);
		} else {
			super.update(shoppingCart);
		}
	}

	/**
	 * Get a {@link ShoppingCart} for a given id and MerchantStore. Will update
	 * the shopping cart prices and items based on the actual inventory. This
	 * method will remove the shopping cart if no items are attached.
	 */

	@Transactional
	public ShoppingCart getById(final Long id, final MerchantStore store) throws ServiceException {

		try {
			ShoppingCart shoppingCart = shoppingCartRepository.findById(store.getId(), id);
			if (shoppingCart == null) {
				return null;
			}
			getPopulatedShoppingCart(shoppingCart);

			if (shoppingCart.isObsolete()) {
				delete(shoppingCart);
				return null;
			} else {
				return shoppingCart;
			}

		} catch (Exception e) {
			throw new ServiceException(e);
		}

	}

	/**
	 * Get a {@link ShoppingCart} for a given id. Will update the shopping cart
	 * prices and items based on the actual inventory. This method will remove
	 * the shopping cart if no items are attached.
	 */

	@Transactional
	public ShoppingCart getById(final Long id) {

		try {
			ShoppingCart shoppingCart = shoppingCartRepository.findOne(id);
			if (shoppingCart == null) {
				return null;
			}
			getPopulatedShoppingCart(shoppingCart);

			if (shoppingCart.isObsolete()) {
				delete(shoppingCart);
				return null;
			} else {
				return shoppingCart;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * Get a {@link ShoppingCart} for a given code. Will update the shopping
	 * cart prices and items based on the actual inventory. This method will
	 * remove the shopping cart if no items are attached.
	 */

	@Transactional
	public ShoppingCart getByCode(final String code, final MerchantStore store) {

		try {
			ShoppingCart shoppingCart = shoppingCartRepository.findByCode(store.getId(), code);
			if (shoppingCart == null) {
				return null;
			}
			getPopulatedShoppingCart(shoppingCart);

			if (shoppingCart.isObsolete()) {
				delete(shoppingCart);
				return null;
			} else {
				return shoppingCart;
			}

		} catch (javax.persistence.NoResultException nre) {
			return null;
		} catch (RuntimeException e) {
			throw new ServiceException(e);
		} catch (Exception ee) {
			throw new ServiceException(ee);
		} catch (Throwable t) {
			throw new ServiceException(t);
		}

	}

	public void deleteCart(final ShoppingCart shoppingCart) throws ServiceException {
		ShoppingCart cart = this.getById(shoppingCart.getId());
		if (cart != null) {
			super.delete(cart);
		}
	}

	@Transactional
	public ShoppingCart getByCustomer(final Customer customer) throws ServiceException {

		try {
			ShoppingCart shoppingCart = shoppingCartRepository.findByCustomer(customer.getId());
			if (shoppingCart == null) {
				return null;
			}
			return getPopulatedShoppingCart(shoppingCart);

		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Transactional(noRollbackFor = { org.springframework.dao.EmptyResultDataAccessException.class })
	private ShoppingCart getPopulatedShoppingCart(final ShoppingCart shoppingCart) {

		try {

			boolean cartIsObsolete = false;
			if (shoppingCart != null) {

				Set<ShoppingCartItem> items = shoppingCart.getLineItems();
				if (items == null || items.size() == 0) {
					shoppingCart.setObsolete(true);
					return shoppingCart;

				}

				// Set<ShoppingCartItem> shoppingCartItems = new
				// HashSet<ShoppingCartItem>();
				for (ShoppingCartItem item : items) {
					log.debug("Populate item " + item.getId());
					getPopulatedItem(item);
					log.debug("Obsolete item ? " + item.isObsolete());
					if (item.isObsolete()) {
						cartIsObsolete = true;
					}
				}

				// shoppingCart.setLineItems(shoppingCartItems);
				boolean refreshCart = false;
				Set<ShoppingCartItem> refreshedItems = new HashSet<ShoppingCartItem>();
				for (ShoppingCartItem item : items) {
					/*
					 * if (!item.isObsolete()) { refreshedItems.add(item); }
					 * else { refreshCart = true; }
					 */
					refreshedItems.add(item);
				}

				// if (refreshCart) {
				shoppingCart.setLineItems(refreshedItems);
				update(shoppingCart);
				// }

				if (cartIsObsolete) {
					shoppingCart.setObsolete(true);
				}
				return shoppingCart;
			}

		} catch (Exception e) {
			log.error(e.getMessage());
			throw new ServiceException(e);
		}

		return shoppingCart;

	}

	public ShoppingCartItem populateShoppingCartItem(final Product product) throws ServiceException {
		Validate.notNull(product, "Product should not be null");
		Validate.notNull(product.getMerchantStore(), "Product.merchantStore should not be null");

		ShoppingCartItem item = new ShoppingCartItem(product);

		// Set<ProductAttribute> productAttributes = product.getAttributes();
		// Set<ShoppingCartAttributeItem> attributesList = new
		// HashSet<ShoppingCartAttributeItem>();
		// if(!CollectionUtils.isEmpty(productAttributes)) {

		// for(ProductAttribute productAttribute : productAttributes) {
		// ShoppingCartAttributeItem attributeItem = new
		// ShoppingCartAttributeItem();
		// attributeItem.setShoppingCartItem(item);
		// attributeItem.setProductAttribute(productAttribute);
		// attributeItem.setProductAttributeId(productAttribute.getId());
		// attributesList.add(attributeItem);

		// }

		// item.setAttributes(attributesList);
		// }

		item.setProductVirtual(product.isProductVirtual());

		// set item price
		FinalPrice price = pricingService.calculateProductPrice(product);
		item.setItemPrice(price.getFinalPrice());
		return item;

	}

	@Transactional
	private void getPopulatedItem(final ShoppingCartItem item) throws Exception {

		Product product = null;

		Long productId = item.getProductId();
		product = productService.findOne(productId);

		if (product == null) {
			item.setObsolete(true);
			return;
		}

		item.setProduct(product);

		if (product.isProductVirtual()) {
			item.setProductVirtual(true);
		}

		Set<ShoppingCartAttributeItem> cartAttributes = item.getAttributes();
		Set<ProductAttribute> productAttributes = product.getAttributes();
		List<ProductAttribute> attributesList = new ArrayList<ProductAttribute>();// attributes
																					// maintained
		List<ShoppingCartAttributeItem> removeAttributesList = new ArrayList<ShoppingCartAttributeItem>();// attributes
																											// to
																											// remove
		// DELETE ORPHEANS MANUALLY
		if ((productAttributes != null && productAttributes.size() > 0)
				|| (cartAttributes != null && cartAttributes.size() > 0)) {
			for (ShoppingCartAttributeItem attribute : cartAttributes) {
				long attributeId = attribute.getProductAttributeId().longValue();
				boolean existingAttribute = false;
				for (ProductAttribute productAttribute : productAttributes) {

					if (productAttribute.getId().longValue() == attributeId) {
						attribute.setProductAttribute(productAttribute);
						attributesList.add(productAttribute);
						existingAttribute = true;
						break;
					}
				}

				if (!existingAttribute) {
					removeAttributesList.add(attribute);
				}

			}
		}

		// cleanup orphean item
		if (CollectionUtils.isNotEmpty(removeAttributesList)) {
			for (ShoppingCartAttributeItem attr : removeAttributesList) {
				shoppingCartAttributeItemRepository.delete(attr);
			}
		}

		// cleanup detached attributes
		if (CollectionUtils.isEmpty(attributesList)) {
			item.setAttributes(null);
		}

		// set item price
		FinalPrice price = pricingService.calculateProductPrice(product, attributesList);
		item.setItemPrice(price.getFinalPrice());
		item.setFinalPrice(price);

		BigDecimal subTotal = item.getItemPrice().multiply(new BigDecimal(item.getQuantity().intValue()));
		item.setSubTotal(subTotal);

	}

	public List<ShippingProduct> createShippingProduct(final ShoppingCart cart) throws ServiceException {
		/**
		 * Determines if products are virtual
		 */
		Set<ShoppingCartItem> items = cart.getLineItems();
		List<ShippingProduct> shippingProducts = null;
		for (ShoppingCartItem item : items) {
			Product product = item.getProduct();
			if (!product.isProductVirtual() && product.isProductShipeable()) {
				if (shippingProducts == null) {
					shippingProducts = new ArrayList<ShippingProduct>();
				}
				ShippingProduct shippingProduct = new ShippingProduct(product);
				shippingProduct.setQuantity(item.getQuantity());
				shippingProduct.setFinalPrice(item.getFinalPrice());
				shippingProducts.add(shippingProduct);
			}
		}

		return shippingProducts;

	}
	/**
	 * Determines if products are free
	 */
	public boolean isFreeShoppingCart(final ShoppingCart cart)  {
		
		Set<ShoppingCartItem> items = cart.getLineItems();
		for (ShoppingCartItem item : items) {
			Product product = item.getProduct();
			FinalPrice finalPrice = pricingService.calculateProductPrice(product);
			if (finalPrice.getFinalPrice().longValue() > 0) {
				return false;
			}
		}

		return true;

	}

	public boolean requiresShipping(final ShoppingCart cart)  {

		Validate.notNull(cart, "Shopping cart cannot be null");
		Validate.notNull(cart.getLineItems(), "ShoppingCart items cannot be null");
		boolean requiresShipping = false;
		for (ShoppingCartItem item : cart.getLineItems()) {
			Product product = item.getProduct();
			if (product.isProductShipeable()) {
				requiresShipping = true;
				break;
			}
		}

		return requiresShipping;

	}

	public void removeShoppingCart(final ShoppingCart cart) throws ServiceException {
		shoppingCartRepository.delete(cart);
	}

	public ShoppingCart mergeShoppingCarts(final ShoppingCart userShoppingModel, final ShoppingCart sessionCart,
			final MerchantStore store) throws Exception {
		if (sessionCart.getCustomerId() != null && sessionCart.getCustomerId() == userShoppingModel.getCustomerId()) {
			log.info("Session Shopping cart belongs to same logged in user");
			if (CollectionUtils.isNotEmpty(userShoppingModel.getLineItems())
					&& CollectionUtils.isNotEmpty(sessionCart.getLineItems())) {
				return userShoppingModel;
			}
		}

		log.info("Starting merging shopping carts");
		if (CollectionUtils.isNotEmpty(sessionCart.getLineItems())) {
			Set<ShoppingCartItem> shoppingCartItemsSet = getShoppingCartItems(sessionCart, store, userShoppingModel);
			boolean duplicateFound = false;
			if (CollectionUtils.isNotEmpty(shoppingCartItemsSet)) {
				for (ShoppingCartItem sessionShoppingCartItem : shoppingCartItemsSet) {
					if (CollectionUtils.isNotEmpty(userShoppingModel.getLineItems())) {
						for (ShoppingCartItem cartItem : userShoppingModel.getLineItems()) {
							if (cartItem.getProduct().getId().longValue() == sessionShoppingCartItem.getProduct()
									.getId().longValue()) {
								if (CollectionUtils.isNotEmpty(cartItem.getAttributes())) {
									if (!duplicateFound) {
										log.info("Dupliate item found..updating exisitng product quantity");
										cartItem.setQuantity(
												cartItem.getQuantity() + sessionShoppingCartItem.getQuantity());
										duplicateFound = true;
										break;
									}
								}
							}
						}
					}
					if (!duplicateFound) {
						log.info("New item found..adding item to Shopping cart");
						userShoppingModel.getLineItems().add(sessionShoppingCartItem);
					}
				}

			}

		}
		log.info("Shopping Cart merged successfully.....");
		saveOrUpdate(userShoppingModel);
		removeShoppingCart(sessionCart);

		return userShoppingModel;
	}

	private Set<ShoppingCartItem> getShoppingCartItems(final ShoppingCart sessionCart, final MerchantStore store,
			final ShoppingCart cartModel) throws Exception {

		Set<ShoppingCartItem> shoppingCartItemsSet = null;
		if (CollectionUtils.isNotEmpty(sessionCart.getLineItems())) {
			shoppingCartItemsSet = new HashSet<ShoppingCartItem>();
			for (ShoppingCartItem shoppingCartItem : sessionCart.getLineItems()) {
				Product product = productService.findOne(shoppingCartItem.getProductId());
				if (product == null) {
					throw new Exception("Item with id " + shoppingCartItem.getProductId() + " does not exist");
				}

				if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
					throw new Exception("Item with id " + shoppingCartItem.getProductId()
							+ " does not belong to merchant " + store.getId());
				}

				ShoppingCartItem item = populateShoppingCartItem(product);
				item.setQuantity(shoppingCartItem.getQuantity());
				item.setShoppingCart(cartModel);

				List<ShoppingCartAttributeItem> cartAttributes = new ArrayList<ShoppingCartAttributeItem>(
						shoppingCartItem.getAttributes());
				if (CollectionUtils.isNotEmpty(cartAttributes)) {
					for (ShoppingCartAttributeItem shoppingCartAttributeItem : cartAttributes) {
						ProductAttribute productAttribute = productAttributeService
								.getById(shoppingCartAttributeItem.getId());
						if (productAttribute != null
								&& productAttribute.getProduct().getId().longValue() == product.getId().longValue()) {

							ShoppingCartAttributeItem attributeItem = new ShoppingCartAttributeItem(item,
									productAttribute);
							if (shoppingCartAttributeItem.getId() > 0) {
								attributeItem.setId(shoppingCartAttributeItem.getId());
							}
							item.addAttributes(attributeItem);

						}
					}
				}

				shoppingCartItemsSet.add(item);
			}

		}
		return shoppingCartItemsSet;
	}

	public boolean isFreeShoppingCart(List<ShoppingCartItem> items) throws ServiceException {
		ShoppingCart cart = new ShoppingCart();
		Set<ShoppingCartItem> cartItems = new HashSet<ShoppingCartItem>(items);
		cart.setLineItems(cartItems);
		return this.isFreeShoppingCart(cart);
	}

	public void deleteShoppingCartItem(Long id) {
		shoppingCartItemRepository.delete(id);
	}

}
