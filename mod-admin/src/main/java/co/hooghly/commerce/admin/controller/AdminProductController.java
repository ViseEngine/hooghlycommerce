package co.hooghly.commerce.admin.controller;

import co.hooghly.commerce.business.CategoryService;
import co.hooghly.commerce.business.ManufacturerService;
import co.hooghly.commerce.business.ProductImageService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.business.ProductTypeService;
import co.hooghly.commerce.business.TaxClassService;
import co.hooghly.commerce.business.utils.CoreConfiguration;
import co.hooghly.commerce.business.utils.ProductPriceUtils;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.CategoryDescription;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Manufacturer;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAttribute;
import co.hooghly.commerce.domain.ProductAvailability;
import co.hooghly.commerce.domain.ProductDescription;
import co.hooghly.commerce.domain.ProductImage;
import co.hooghly.commerce.domain.ProductImageDescription;
import co.hooghly.commerce.domain.ProductPrice;
import co.hooghly.commerce.domain.ProductPriceDescription;
import co.hooghly.commerce.domain.ProductRelationship;
import co.hooghly.commerce.domain.ProductType;
import co.hooghly.commerce.domain.TaxClass;

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
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.*;

@Controller
@Slf4j
public class AdminProductController {

	@Autowired
	private ProductService productService;

	@Autowired
	private ManufacturerService manufacturerService;

	@Autowired
	private ProductTypeService productTypeService;

	@Autowired
	private ProductImageService productImageService;

	@Autowired
	private TaxClassService taxClassService;

	@Autowired
	private ProductPriceUtils priceUtil;

	@Autowired
	LabelUtils messages;

	@Autowired
	private CoreConfiguration configuration;

	@Autowired
	CategoryService categoryService;

	@RequestMapping(value = "/admin/products/editProduct.html", method = RequestMethod.GET)
	public String displayProductEdit(@RequestParam("id") long productId, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return displayProduct(productId, model, request, response);

	}

	@RequestMapping(value = "/admin/products/viewEditProduct.html", method = RequestMethod.GET)
	public String displayProductEdit(@RequestParam("sku") String sku, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Language language = (Language) request.getAttribute("LANGUAGE");
		Product dbProduct = productService.getByCode(sku, language);

		long productId = -1;// non existent
		if (dbProduct != null) {
			productId = dbProduct.getId();
		}

		return displayProduct(productId, model, request, response);
	}

	@RequestMapping(value = "/admin/products/createProduct.html", method = RequestMethod.GET)
	public String displayProductCreate(Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return displayProduct(null, model, request, response);

	}

	private String displayProduct(Long productId, Model model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language) request.getAttribute("LANGUAGE");

		List<Manufacturer> manufacturers = manufacturerService.listByStore(store, language);

		List<ProductType> productTypes = productTypeService.list();

		List<TaxClass> taxClasses = taxClassService.listByStore(store);

		List<Language> languages = store.getLanguages();

		co.hooghly.commerce.domain.admin.Product product = new co.hooghly.commerce.domain.admin.Product();
		List<ProductDescription> descriptions = new ArrayList<ProductDescription>();

		if (productId != null && productId != 0) {// edit mode

			Product dbProduct = productService.getById(productId);

			if (dbProduct == null || dbProduct.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				return "redirect:/admin/products/products.html";
			}

			product.setProduct(dbProduct);
			Set<ProductDescription> productDescriptions = dbProduct.getDescriptions();

			for (Language l : languages) {

				ProductDescription productDesc = null;
				for (ProductDescription desc : productDescriptions) {

					Language lang = desc.getLanguage();
					if (lang.getCode().equals(l.getCode())) {
						productDesc = desc;
					}

				}

				if (productDesc == null) {
					productDesc = new ProductDescription();
					productDesc.setLanguage(l);
				}

				descriptions.add(productDesc);

			}

			for (ProductImage image : dbProduct.getImages()) {
				if (image.isDefaultImage()) {
					product.setProductImage(image);
					break;
				}

			}

			ProductAvailability productAvailability = null;
			ProductPrice productPrice = null;

			Set<ProductAvailability> availabilities = dbProduct.getAvailabilities();
			if (availabilities != null && availabilities.size() > 0) {

				for (ProductAvailability availability : availabilities) {
					if (availability.getRegion().equals(co.hooghly.commerce.business.Constants.ALL_REGIONS)) {
						productAvailability = availability;
						Set<ProductPrice> prices = availability.getPrices();
						for (ProductPrice price : prices) {
							if (price.isDefaultPrice()) {
								productPrice = price;
								product.setProductPrice(
										priceUtil.getAdminFormatedAmount(store, productPrice.getProductPriceAmount()));
							}
						}
					}
				}
			}

			if (productAvailability == null) {
				productAvailability = new ProductAvailability();
			}

			if (productPrice == null) {
				productPrice = new ProductPrice();
			}

			product.setAvailability(productAvailability);
			product.setPrice(productPrice);
			product.setDescriptions(descriptions);

			//product.setDateAvailable(DateUtil.formatDate(dbProduct.getDateAvailable()));

		} else {

			for (Language l : languages) {

				ProductDescription desc = new ProductDescription();
				desc.setLanguage(l);
				descriptions.add(desc);

			}

			Product prod = new Product();

			prod.setAvailable(true);

			ProductAvailability productAvailability = new ProductAvailability();
			ProductPrice price = new ProductPrice();
			product.setPrice(price);
			product.setAvailability(productAvailability);
			product.setProduct(prod);
			product.setDescriptions(descriptions);
			//product.setDateAvailable(DateUtil.formatDate(new Date()));

		}

		model.addAttribute("product", product);
		model.addAttribute("manufacturers", manufacturers);
		model.addAttribute("productTypes", productTypes);
		model.addAttribute("taxClasses", taxClasses);
		return "admin-products-edit";
	}

	
	@RequestMapping(value = "/admin/products/save.html", method = RequestMethod.POST)
	public String saveProduct(@Valid @ModelAttribute("product") co.hooghly.commerce.domain.admin.Product product,
			BindingResult result, Model model, HttpServletRequest request, Locale locale) throws Exception {

		Language language = (Language) request.getAttribute("LANGUAGE");

		

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		List<Manufacturer> manufacturers = manufacturerService.listByStore(store, language);

		List<ProductType> productTypes = productTypeService.list();

		List<TaxClass> taxClasses = taxClassService.listByStore(store);

		List<Language> languages = store.getLanguages();

		model.addAttribute("manufacturers", manufacturers);
		model.addAttribute("productTypes", productTypes);
		model.addAttribute("taxClasses", taxClasses);

		// validate price
		BigDecimal submitedPrice = null;
		try {
			submitedPrice = priceUtil.getAmount(product.getProductPrice());
		} catch (Exception e) {
			ObjectError error = new ObjectError("productPrice",
					messages.getMessage("NotEmpty.product.productPrice", locale));
			result.addError(error);
		}
		Date date = new Date();
		if (!StringUtils.isBlank(product.getDateAvailable())) {
			try {
				//date = DateUtil.getDate(product.getDateAvailable());
				product.getAvailability().setProductDateAvailable(date);
				//product.setDateAvailable(DateUtil.formatDate(date));
			} catch (Exception e) {
				ObjectError error = new ObjectError("dateAvailable",
						messages.getMessage("message.invalid.date", locale));
				result.addError(error);
			}
		}

		// validate image
		if (product.getImage() != null && !product.getImage().isEmpty()) {

			try {

				String maxHeight = configuration.getProperty("PRODUCT_IMAGE_MAX_HEIGHT_SIZE");
				String maxWidth = configuration.getProperty("PRODUCT_IMAGE_MAX_WIDTH_SIZE");
				String maxSize = configuration.getProperty("PRODUCT_IMAGE_MAX_SIZE");

				BufferedImage image = ImageIO.read(product.getImage().getInputStream());

				if (!StringUtils.isBlank(maxHeight)) {

					int maxImageHeight = Integer.parseInt(maxHeight);
					if (image.getHeight() > maxImageHeight) {
						ObjectError error = new ObjectError("image",
								messages.getMessage("message.image.height", locale) + " {" + maxHeight + "}");
						result.addError(error);
					}

				}

				if (!StringUtils.isBlank(maxWidth)) {

					int maxImageWidth = Integer.parseInt(maxWidth);
					if (image.getWidth() > maxImageWidth) {
						ObjectError error = new ObjectError("image",
								messages.getMessage("message.image.width", locale) + " {" + maxWidth + "}");
						result.addError(error);
					}

				}

				if (!StringUtils.isBlank(maxSize)) {

					int maxImageSize = Integer.parseInt(maxSize);
					if (product.getImage().getSize() > maxImageSize) {
						ObjectError error = new ObjectError("image",
								messages.getMessage("message.image.size", locale) + " {" + maxSize + "}");
						result.addError(error);
					}

				}

			} catch (Exception e) {
				log.error("Cannot validate product image", e);
			}

		}

		if (result.hasErrors()) {
			return "admin-products-edit";
		}

		Product newProduct = product.getProduct();
		ProductAvailability newProductAvailability = null;
		ProductPrice newProductPrice = null;

		Set<ProductPriceDescription> productPriceDescriptions = null;

		// get tax class
		// TaxClass taxClass = newProduct.getTaxClass();
		// TaxClass dbTaxClass = taxClassService.getById(taxClass.getId());
		Set<ProductPrice> prices = new HashSet<ProductPrice>();
		Set<ProductAvailability> availabilities = new HashSet<ProductAvailability>();

		if (product.getProduct().getId() != null && product.getProduct().getId().longValue() > 0) {

			// get actual product
			newProduct = productService.getById(product.getProduct().getId());
			if (newProduct != null && newProduct.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				return "redirect:/admin/products/products.html";
			}

			// copy properties
			newProduct.setSku(product.getProduct().getSku());
			newProduct.setRefSku(product.getProduct().getRefSku());
			newProduct.setAvailable(product.getProduct().isAvailable());
			newProduct.setDateAvailable(date);
			newProduct.setManufacturer(product.getProduct().getManufacturer());
			newProduct.setType(product.getProduct().getType());
			newProduct.setProductHeight(product.getProduct().getProductHeight());
			newProduct.setProductLength(product.getProduct().getProductLength());
			newProduct.setProductWeight(product.getProduct().getProductWeight());
			newProduct.setProductWidth(product.getProduct().getProductWidth());
			newProduct.setProductVirtual(product.getProduct().isProductVirtual());
			newProduct.setProductShipeable(product.getProduct().isProductShipeable());
			newProduct.setTaxClass(product.getProduct().getTaxClass());
			newProduct.setSortOrder(product.getProduct().getSortOrder());

			Set<ProductAvailability> avails = newProduct.getAvailabilities();
			if (avails != null && avails.size() > 0) {

				for (ProductAvailability availability : avails) {
					if (availability.getRegion().equals(co.hooghly.commerce.business.Constants.ALL_REGIONS)) {

						newProductAvailability = availability;
						Set<ProductPrice> productPrices = availability.getPrices();

						for (ProductPrice price : productPrices) {
							if (price.isDefaultPrice()) {
								newProductPrice = price;
								newProductPrice.setProductPriceAmount(submitedPrice);
								productPriceDescriptions = price.getDescriptions();
							} else {
								prices.add(price);
							}
						}
					} else {
						availabilities.add(availability);
					}
				}
			}

			for (ProductImage image : newProduct.getImages()) {
				if (image.isDefaultImage()) {
					product.setProductImage(image);
				}
			}
		}

		if (newProductPrice == null) {
			newProductPrice = new ProductPrice();
			newProductPrice.setDefaultPrice(true);
			newProductPrice.setProductPriceAmount(submitedPrice);
		}

		if (product.getProductImage() != null && product.getProductImage().getId() == null) {
			product.setProductImage(null);
		}

		if (productPriceDescriptions == null) {
			productPriceDescriptions = new HashSet<ProductPriceDescription>();
			for (ProductDescription description : product.getDescriptions()) {
				ProductPriceDescription ppd = new ProductPriceDescription();
				ppd.setProductPrice(newProductPrice);
				ppd.setLanguage(description.getLanguage());
				ppd.setName(ProductPriceDescription.DEFAULT_PRICE_DESCRIPTION);
				productPriceDescriptions.add(ppd);
			}
			newProductPrice.setDescriptions(productPriceDescriptions);
		}

		newProduct.setMerchantStore(store);

		if (newProductAvailability == null) {
			newProductAvailability = new ProductAvailability();
		}

		newProductAvailability.setProductQuantity(product.getAvailability().getProductQuantity());
		newProductAvailability.setProductQuantityOrderMin(product.getAvailability().getProductQuantityOrderMin());
		newProductAvailability.setProductQuantityOrderMax(product.getAvailability().getProductQuantityOrderMax());
		newProductAvailability.setProduct(newProduct);
		newProductAvailability.setPrices(prices);
		availabilities.add(newProductAvailability);

		newProductPrice.setProductAvailability(newProductAvailability);
		prices.add(newProductPrice);

		newProduct.setAvailabilities(availabilities);

		Set<ProductDescription> descriptions = new HashSet<ProductDescription>();
		if (product.getDescriptions() != null && product.getDescriptions().size() > 0) {

			for (ProductDescription description : product.getDescriptions()) {
				description.setProduct(newProduct);
				descriptions.add(description);

			}
		}

		newProduct.setDescriptions(descriptions);
		//product.setDateAvailable(DateUtil.formatDate(date));

		if (product.getImage() != null && !product.getImage().isEmpty()) {

			String imageName = product.getImage().getOriginalFilename();

			ProductImage productImage = new ProductImage();
			productImage.setDefaultImage(true);
			// productImage.setImage(product.getImage().getInputStream());
			productImage.setProductImage(imageName);

			List<ProductImageDescription> imagesDescriptions = new ArrayList<ProductImageDescription>();

			for (Language l : languages) {

				ProductImageDescription imageDescription = new ProductImageDescription();
				imageDescription.setName(imageName);
				imageDescription.setLanguage(l);
				imageDescription.setProductImage(productImage);
				imagesDescriptions.add(imageDescription);

			}

			productImage.setDescriptions(imagesDescriptions);
			productImage.setProduct(newProduct);

			newProduct.getImages().add(productImage);

			// productService.saveOrUpdate(newProduct);

			// product displayed
			product.setProductImage(productImage);

		} // else {

		// productService.saveOrUpdate(newProduct);

		// }

		productService.create(newProduct);
		model.addAttribute("success", "success");

		return "admin-products-edit";
	}

	/**
	 * Creates a duplicate product with the same inner object graph Will ignore
	 * SKU, reviews and images
	 * 
	 * @param id
	 * @param result
	 * @param model
	 * @param request
	 * @param locale
	 * @return
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/admin/products/product/duplicate.html", method = RequestMethod.POST)
	public String duplicateProduct(@ModelAttribute("productId") Long id, BindingResult result, Model model,
			HttpServletRequest request, Locale locale) throws Exception {

		Language language = (Language) request.getAttribute("LANGUAGE");

		

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		List<Manufacturer> manufacturers = manufacturerService.listByStore(store, language);
		List<ProductType> productTypes = productTypeService.list();
		List<TaxClass> taxClasses = taxClassService.listByStore(store);

		model.addAttribute("manufacturers", manufacturers);
		model.addAttribute("productTypes", productTypes);
		model.addAttribute("taxClasses", taxClasses);

		Product dbProduct = productService.getById(id);
		Product newProduct = new Product();

		if (dbProduct == null || dbProduct.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			return "redirect:/admin/products/products.html";
		}

		// Make a copy of the product
		co.hooghly.commerce.domain.admin.Product product = new co.hooghly.commerce.domain.admin.Product();

		Set<ProductAvailability> availabilities = new HashSet<ProductAvailability>();
		// availability - price
		for (ProductAvailability pAvailability : dbProduct.getAvailabilities()) {

			ProductAvailability availability = new ProductAvailability();
			availability.setProductDateAvailable(pAvailability.getProductDateAvailable());
			availability.setProductIsAlwaysFreeShipping(pAvailability.getProductIsAlwaysFreeShipping());
			availability.setProductQuantity(pAvailability.getProductQuantity());
			availability.setProductQuantityOrderMax(pAvailability.getProductQuantityOrderMax());
			availability.setProductQuantityOrderMin(pAvailability.getProductQuantityOrderMin());
			availability.setProductStatus(pAvailability.getProductStatus());
			availability.setRegion(pAvailability.getRegion());
			availability.setRegionVariant(pAvailability.getRegionVariant());

			Set<ProductPrice> prices = pAvailability.getPrices();
			for (ProductPrice pPrice : prices) {

				ProductPrice price = new ProductPrice();
				price.setDefaultPrice(pPrice.isDefaultPrice());
				price.setProductPriceAmount(pPrice.getProductPriceAmount());
				price.setProductAvailability(availability);
				price.setProductPriceSpecialAmount(pPrice.getProductPriceSpecialAmount());
				price.setProductPriceSpecialEndDate(pPrice.getProductPriceSpecialEndDate());
				price.setProductPriceSpecialStartDate(pPrice.getProductPriceSpecialStartDate());
				price.setProductPriceType(pPrice.getProductPriceType());

				Set<ProductPriceDescription> priceDescriptions = new HashSet<ProductPriceDescription>();
				// price descriptions
				for (ProductPriceDescription pPriceDescription : pPrice.getDescriptions()) {

					ProductPriceDescription productPriceDescription = new ProductPriceDescription();
					productPriceDescription.setAuditSection(pPriceDescription.getAuditSection());
					productPriceDescription.setDescription(pPriceDescription.getDescription());
					productPriceDescription.setName(pPriceDescription.getName());
					productPriceDescription.setLanguage(pPriceDescription.getLanguage());
					productPriceDescription.setProductPrice(price);
					priceDescriptions.add(productPriceDescription);

				}
				price.setDescriptions(priceDescriptions);
				if (price.isDefaultPrice()) {
					product.setPrice(price);
					product.setProductPrice(priceUtil.getAdminFormatedAmount(store, price.getProductPriceAmount()));
				}

				availability.getPrices().add(price);
			}

			if (availability.getRegion().equals(co.hooghly.commerce.business.Constants.ALL_REGIONS)) {
				product.setAvailability(availability);
			}

			availabilities.add(availability);
		}

		newProduct.setAvailabilities(availabilities);

		// attributes
		Set<ProductAttribute> attributes = new HashSet<ProductAttribute>();
		for (ProductAttribute pAttribute : dbProduct.getAttributes()) {

			ProductAttribute attribute = new ProductAttribute();
			attribute.setAttributeDefault(pAttribute.getAttributeDefault());
			attribute.setAttributeDiscounted(pAttribute.getAttributeDiscounted());
			attribute.setAttributeDisplayOnly(pAttribute.getAttributeDisplayOnly());
			attribute.setAttributeRequired(pAttribute.getAttributeRequired());
			attribute.setProductAttributePrice(pAttribute.getProductAttributePrice());
			attribute.setProductAttributeIsFree(pAttribute.getProductAttributeIsFree());
			attribute.setProductAttributeWeight(pAttribute.getProductAttributeWeight());
			attribute.setProductOption(pAttribute.getProductOption());
			attribute.setProductOptionSortOrder(pAttribute.getProductOptionSortOrder());
			attribute.setProductOptionValue(pAttribute.getProductOptionValue());
			attributes.add(attribute);

		}
		newProduct.setAttributes(attributes);

		// relationships
		Set<ProductRelationship> relationships = new HashSet<ProductRelationship>();
		for (ProductRelationship pRelationship : dbProduct.getRelationships()) {

			ProductRelationship relationship = new ProductRelationship();
			relationship.setActive(pRelationship.isActive());
			relationship.setCode(pRelationship.getCode());
			relationship.setRelatedProduct(pRelationship.getRelatedProduct());
			relationship.setStore(store);
			relationships.add(relationship);

		}

		newProduct.setRelationships(relationships);

		// product description
		Set<ProductDescription> descsset = new HashSet<ProductDescription>();
		List<ProductDescription> desclist = new ArrayList<ProductDescription>();
		Set<ProductDescription> descriptions = dbProduct.getDescriptions();
		for (ProductDescription pDescription : descriptions) {

			ProductDescription description = new ProductDescription();
			description.setAuditSection(pDescription.getAuditSection());
			description.setName(pDescription.getName());
			description.setDescription(pDescription.getDescription());
			description.setLanguage(pDescription.getLanguage());
			description.setMetatagDescription(pDescription.getMetatagDescription());
			description.setMetatagKeywords(pDescription.getMetatagKeywords());
			description.setMetatagTitle(pDescription.getMetatagTitle());
			descsset.add(description);
			desclist.add(description);
		}
		newProduct.setDescriptions(descsset);
		product.setDescriptions(desclist);

		// product
		newProduct.setAuditSection(dbProduct.getAuditSection());
		newProduct.setAvailable(dbProduct.isAvailable());

		// copy
		// newProduct.setCategories(dbProduct.getCategories());
		newProduct.setDateAvailable(dbProduct.getDateAvailable());
		newProduct.setManufacturer(dbProduct.getManufacturer());
		newProduct.setMerchantStore(store);
		newProduct.setProductHeight(dbProduct.getProductHeight());
		newProduct.setProductIsFree(dbProduct.isProductIsFree());
		newProduct.setProductLength(dbProduct.getProductLength());
		newProduct.setProductOrdered(dbProduct.getProductOrdered());
		newProduct.setProductWeight(dbProduct.getProductWeight());
		newProduct.setProductWidth(dbProduct.getProductWidth());
		newProduct.setSortOrder(dbProduct.getSortOrder());
		newProduct.setTaxClass(dbProduct.getTaxClass());
		newProduct.setType(dbProduct.getType());
		newProduct.setSku(UUID.randomUUID().toString().replace("-", ""));
		newProduct.setProductVirtual(dbProduct.isProductVirtual());
		newProduct.setProductShipeable(dbProduct.isProductShipeable());

		productService.update(newProduct);

		Set<Category> categories = dbProduct.getCategories();
		for (Category category : categories) {
			Category categoryCopy = categoryService.findOne(category.getId());
			newProduct.getCategories().add(categoryCopy);
			productService.update(newProduct);
		}

		product.setProduct(newProduct);
		model.addAttribute("product", product);
		model.addAttribute("success", "success");

		return "redirect:/admin/products/editProduct.html?id=" + newProduct.getId();
	}

	/**
	 * Removes a product image based on the productimage id
	 * 
	 * @param request
	 * @param response
	 * @param locale
	 * @return
	 */
	@PreAuthorize("hasRole('PRODUCTS')")
	@RequestMapping(value = "/admin/products/product/removeImage.html")
	public @ResponseBody ResponseEntity<String> removeImage(HttpServletRequest request, HttpServletResponse response,
			Locale locale) {
		String iid = request.getParameter("imageId");

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		AjaxResponse resp = new AjaxResponse();

		try {

			Long id = Long.parseLong(iid);
			ProductImage productImage = productImageService.getById(id);
			if (productImage == null
					|| productImage.getProduct().getMerchantStore().getId().intValue() != store.getId().intValue()) {

				resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);

			} else {

				productImageService.removeProductImage(productImage);
				resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

			}

		} catch (Exception e) {
			log.error("Error while deleting product", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}

		String returnString = resp.toJSONString();
		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
	}

	/**
	 * List all categories and let the merchant associate the product to a
	 * category
	 * 
	 * @param productId
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/admin/products/displayProductToCategories.html", method = RequestMethod.GET)
	public String displayAddProductToCategories(@RequestParam("id") long productId, Model model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language) request.getAttribute("LANGUAGE");

		// get the product and validate it belongs to the current merchant
		Product product = productService.getById(productId);

		if (product == null) {
			return "redirect:/admin/products/products.html";
		}

		if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			return "redirect:/admin/products/products.html";
		}

		// get parent categories
		List<Category> categories = categoryService.listByStore(store, language);

		model.addAttribute("product", product);
		model.addAttribute("categories", categories);
		return "catalogue-product-categories";

	}

	/**
	 * List all categories associated to a Product
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/admin/product-categories/paging.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pageProductCategories(HttpServletRequest request,
			HttpServletResponse response) {

		String sProductId = request.getParameter("productId");
		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		AjaxResponse resp = new AjaxResponse();

		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		Long productId;
		Product product = null;

		try {
			productId = Long.parseLong(sProductId);
		} catch (Exception e) {
			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorString("Product id is not valid");
			String returnString = resp.toJSONString();
			return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
		}

		try {

			product = productService.getById(productId);

			if (product == null) {
				resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
			}

			if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
				resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
				resp.setErrorString("Product id is not valid");
				String returnString = resp.toJSONString();
				return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
			}

			Language language = (Language) request.getAttribute("LANGUAGE");

			Set<Category> categories = product.getCategories();

			for (Category category : categories) {
				Map entry = new HashMap();
				entry.put("categoryId", category.getId());

				List<CategoryDescription> descriptions = category.getDescriptions();
				String categoryName = category.getDescriptions().get(0).getName();
				for (CategoryDescription description : descriptions) {
					if (description.getLanguage().getCode().equals(language.getCode())) {
						categoryName = description.getName();
					}
				}
				entry.put("name", categoryName);
				resp.addDataEntry(entry);
			}

			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_SUCCESS);

		} catch (Exception e) {
			log.error("Error while paging products", e);
			resp.setStatus(AjaxPageableResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}

		String returnString = resp.toJSONString();
		return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);

	}

	
	@RequestMapping(value = "/admin/product-categories/remove.html", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> deleteProductFromCategory(HttpServletRequest request,
			HttpServletResponse response, Locale locale) {
		String sCategoryid = request.getParameter("categoryId");
		String sProductId = request.getParameter("productId");

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);

		AjaxResponse resp = new AjaxResponse();

		final HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);

		try {

			Long categoryId = Long.parseLong(sCategoryid);
			Long productId = Long.parseLong(sProductId);

			Category category = categoryService.findOne(categoryId);
			Product product = productService.getById(productId);

			if (category == null || category.getMerchantStore().getId() != store.getId()) {

				resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
			}

			if (product == null || product.getMerchantStore().getId() != store.getId()) {

				resp.setStatusMessage(messages.getMessage("message.unauthorized", locale));
				resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
				String returnString = resp.toJSONString();
				return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
			}

			product.getCategories().remove(category);
			productService.update(product);

			resp.setStatus(AjaxResponse.RESPONSE_OPERATION_COMPLETED);

		} catch (Exception e) {
			log.error("Error while deleting category", e);
			resp.setStatus(AjaxResponse.RESPONSE_STATUS_FAIURE);
			resp.setErrorMessage(e);
		}

		String returnString = resp.toJSONString();

		return new ResponseEntity<String>(returnString, httpHeaders, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/admin/products/addProductToCategories.html", method = RequestMethod.POST)
	public String addProductToCategory(@RequestParam("productId") long productId, @RequestParam("id") long categoryId,
			Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		
		MerchantStore store = (MerchantStore) request.getAttribute(Constants.ADMIN_STORE);
		Language language = (Language) request.getAttribute("LANGUAGE");

		// get the product and validate it belongs to the current merchant
		Product product = productService.getById(productId);

		if (product == null) {
			return "redirect:/admin/products/products.html";
		}

		if (product.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			return "redirect:/admin/products/products.html";
		}

		// get parent categories
		List<Category> categories = categoryService.listByStore(store, language);

		Category category = categoryService.findOne(categoryId);

		if (category == null) {
			return "redirect:/admin/products/products.html";
		}

		if (category.getMerchantStore().getId().intValue() != store.getId().intValue()) {
			return "redirect:/admin/products/products.html";
		}

		product.getCategories().add(category);

		productService.update(product);

		model.addAttribute("product", product);
		model.addAttribute("categories", categories);

		return "catalogue-product-categories";

	}

	
}