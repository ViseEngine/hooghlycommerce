package co.hooghly.commerce.shop.controller;


import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.ProductAttributeService;
import co.hooghly.commerce.business.ProductRelationshipService;
import co.hooghly.commerce.business.ProductReviewService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.FinalPrice;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductAttribute;
import co.hooghly.commerce.domain.ProductOptionDescription;
import co.hooghly.commerce.domain.ProductOptionValue;
import co.hooghly.commerce.domain.ProductOptionValueDescription;
import co.hooghly.commerce.domain.ProductRelationship;
import co.hooghly.commerce.domain.ProductRelationshipType;
import co.hooghly.commerce.domain.ProductReview;
import co.hooghly.commerce.util.BreadcrumbsUtils;
import co.hooghly.commerce.util.ImageFilePath;
import co.hooghly.commerce.util.PageBuilderUtils;
import co.hooghly.commerce.web.populator.ReadableFinalPricePopulator;
import co.hooghly.commerce.web.populator.ReadableProductReviewPopulator;
import co.hooghly.commerce.web.ui.Attribute;
import co.hooghly.commerce.web.ui.AttributeValue;
import co.hooghly.commerce.web.ui.Breadcrumb;
import co.hooghly.commerce.web.ui.ReadableProductPrice;
import co.hooghly.commerce.web.ui.ReadableProductReview;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Populates the product details page
 * 
 *
 */
@Controller
@RequestMapping("/shop/product")
@Slf4j
public class ProductController {

	@Inject
	private ProductService productService;

	@Inject
	private ProductAttributeService productAttributeService;

	@Inject
	private ProductRelationshipService productRelationshipService;

	@Inject
	private PricingService pricingService;

	@Inject
	private ProductReviewService productReviewService;

	

	@Inject
	private BreadcrumbsUtils breadcrumbsUtils;

	//@Inject
	//@Qualifier("img")
	private ImageFilePath imageUtils;

	/**
	 * Display product details with reference to caller page
	 */
	@RequestMapping("/{friendlyUrl}/ref={ref}")
	public String displayProductWithReference(@PathVariable final String friendlyUrl, @PathVariable final String ref,
			Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		return display(ref, friendlyUrl, model, request, response, locale);
	}

	/**
	 * Display product details no reference
	 */
	@RequestMapping("/{friendlyUrl}")
	public String displayProduct(@PathVariable final String friendlyUrl, Model model, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception {
		log.info("==== display product details =====" + friendlyUrl);
		return display(null, friendlyUrl, model, request, response, locale);
	}

	public String display(final String reference, final String friendlyUrl, Model model, HttpServletRequest request,
			HttpServletResponse response, Locale locale) throws Exception {

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language) request.getAttribute("LANGUAGE");

		Product product = productService.getBySeUrl(store, "/shop/product/" + friendlyUrl, locale);
		model.addAttribute("product", product);

		if (product == null) {
			return PageBuilderUtils.build404(store);
		}

		FinalPrice finalPrice = pricingService.calculateProductPrice(product);
		model.addAttribute("finalPrice", finalPrice.getFinalPrice()+"");

		// meta information
		//PageInformation pageInformation = new PageInformation();
		//pageInformation.setPageDescription(product.getDescription().getMetaDescription());
		//pageInformation.setPageKeywords(productProxy.getDescription().getKeyWords());
		//pageInformation.setPageTitle(productProxy.getDescription().getTitle());
		//pageInformation.setPageUrl(productProxy.getDescription().getFriendlyUrl());

		//request.setAttribute(Constants.REQUEST_PAGE_INFORMATION, pageInformation);

		Breadcrumb breadCrumb = breadcrumbsUtils.buildProductBreadcrumb(reference, product, store, language,
				request.getContextPath());
		request.getSession().setAttribute(Constants.BREADCRUMB, breadCrumb);
		request.setAttribute(Constants.BREADCRUMB, breadCrumb);

		
		List<Product> relatedItems = relatedItems(store, product, language);

		

		model.addAttribute("relatedProducts", relatedItems);
		Set<ProductAttribute> attributes = product.getAttributes();

		// split read only and options
		Map<Long, Attribute> readOnlyAttributes = null;
		Map<Long, Attribute> selectableOptions = null;

		if (!CollectionUtils.isEmpty(attributes)) {
			for (ProductAttribute attribute : attributes) {
				Attribute attr = null;
				AttributeValue attrValue = new AttributeValue();
				ProductOptionValue optionValue = attribute.getProductOptionValue();

				if (attribute.isAttributeDisplayOnly() == true) {// read only
																	// attribute
					if (readOnlyAttributes == null) {
						readOnlyAttributes = new TreeMap<Long, Attribute>();
					}
					attr = readOnlyAttributes.get(attribute.getProductOption().getId());
					if (attr == null) {
						attr = createAttribute(attribute, language);
					}
					if (attr != null) {
						readOnlyAttributes.put(attribute.getProductOption().getId(), attr);
						attr.setReadOnlyValue(attrValue);
					}
				} else {// selectable option
					if (selectableOptions == null) {
						selectableOptions = new TreeMap<Long, Attribute>();
					}
					attr = selectableOptions.get(attribute.getProductOption().getId());
					if (attr == null) {
						attr = createAttribute(attribute, language);
					}
					if (attr != null) {
						selectableOptions.put(attribute.getProductOption().getId(), attr);
					}
				}

				attrValue.setDefaultAttribute(attribute.isAttributeDefault());
				attrValue.setId(attribute.getId());// id of the attribute
				attrValue.setLanguage(language.getCode());
				if (attribute.getProductAttributePrice() != null
						&& attribute.getProductAttributePrice().doubleValue() > 0) {
					String formatedPrice = pricingService.getDisplayAmount(attribute.getProductAttributePrice(), store);
					attrValue.setPrice(formatedPrice);
				}

				if (!StringUtils.isBlank(attribute.getProductOptionValue().getProductOptionValueImage())) {
					attrValue.setImage(imageUtils.buildProductPropertyImageUtils(store,
							attribute.getProductOptionValue().getProductOptionValueImage()));
				}

				List<ProductOptionValueDescription> descriptions = optionValue.getDescriptionsSettoList();
				ProductOptionValueDescription description = null;
				if (descriptions != null && descriptions.size() > 0) {
					description = descriptions.get(0);
					if (descriptions.size() > 1) {
						for (ProductOptionValueDescription optionValueDescription : descriptions) {
							if (optionValueDescription.getLanguage().getId().intValue() == language.getId()
									.intValue()) {
								description = optionValueDescription;
								break;
							}
						}
					}
				}
				attrValue.setName(description.getName());
				attrValue.setDescription(description.getDescription());
				List<AttributeValue> attrs = attr.getValues();
				if (attrs == null) {
					attrs = new ArrayList<AttributeValue>();
					attr.setValues(attrs);
				}
				attrs.add(attrValue);
			}
		}

		List<ProductReview> reviews = productReviewService.getByProduct(product, language);
		if (!CollectionUtils.isEmpty(reviews)) {
			List<ReadableProductReview> revs = new ArrayList<ReadableProductReview>();
			ReadableProductReviewPopulator reviewPopulator = new ReadableProductReviewPopulator();
			for (ProductReview review : reviews) {
				ReadableProductReview rev = new ReadableProductReview();
				reviewPopulator.populate(review, rev, store, language);
				revs.add(rev);
			}
			model.addAttribute("reviews", revs);
		}

		List<Attribute> attributesList = null;
		if (readOnlyAttributes != null) {
			attributesList = new ArrayList<Attribute>(readOnlyAttributes.values());
		}

		List<Attribute> optionsList = null;
		if (selectableOptions != null) {
			optionsList = new ArrayList<Attribute>(selectableOptions.values());
		}

		model.addAttribute("attributes", attributesList);
		model.addAttribute("options", optionsList);

		//model.addAttribute("product", productProxy);

		/** template **/
		String template = ControllerConstants.Tiles.Product.product + "." + store.getStoreTemplate();

		log.info("Product - {}", ControllerConstants.Tiles.Product.product);

		//log.info("store.getStoreTemplate() - {}", store.getStoreTemplate());
		return "productdetails";
	}

	@RequestMapping(value = { "/{productId}/calculatePrice.json" }, method = RequestMethod.POST)
	public @ResponseBody ReadableProductPrice calculatePrice(
			@RequestParam(value = "attributeIds[]") Long[] attributeIds, @PathVariable final Long productId,
			final HttpServletRequest request, final HttpServletResponse response, final Locale locale)
			throws Exception {

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);
		Language language = (Language) request.getAttribute("LANGUAGE");

		Product product = productService.findOne(productId);

		@SuppressWarnings("unchecked")
		List<Long> ids = new ArrayList<Long>(Arrays.asList(attributeIds));
		List<ProductAttribute> attributes = productAttributeService.getByAttributeIds(store, product, ids);

		for (ProductAttribute attribute : attributes) {
			if (attribute.getProduct().getId().longValue() != productId.longValue()) {
				return null;
			}
		}

		FinalPrice price = pricingService.calculateProductPrice(product, attributes);
		ReadableProductPrice readablePrice = new ReadableProductPrice();
		ReadableFinalPricePopulator populator = new ReadableFinalPricePopulator();
		populator.setPricingService(pricingService);
		populator.populate(price, readablePrice, store, language);
		return readablePrice;

	}

	private Attribute createAttribute(ProductAttribute productAttribute, Language language) {

		Attribute attribute = new Attribute();
		attribute.setId(productAttribute.getProductOption().getId());// attribute
																		// of
																		// the
																		// option
		attribute.setType(productAttribute.getProductOption().getProductOptionType());
		List<ProductOptionDescription> descriptions = productAttribute.getProductOption().getDescriptionsSettoList();
		ProductOptionDescription description = null;
		if (descriptions != null && descriptions.size() > 0) {
			description = descriptions.get(0);
			if (descriptions.size() > 1) {
				for (ProductOptionDescription optionDescription : descriptions) {
					if (optionDescription.getLanguage().getId().intValue() == language.getId().intValue()) {
						description = optionDescription;
						break;
					}
				}
			}
		}

		if (description == null) {
			return null;
		}

		attribute.setType(productAttribute.getProductOption().getProductOptionType());
		attribute.setLanguage(language.getCode());
		attribute.setName(description.getName());
		attribute.setCode(productAttribute.getProductOption().getCode());

		return attribute;

	}

	private List<Product> relatedItems(MerchantStore store, Product product, Language language)
			 {

		
		List<ProductRelationship> relatedItems = productRelationshipService.getByType(store, product,
				ProductRelationshipType.RELATED_ITEM);
		
		List<Product> relatedProducts = new ArrayList<Product>();
		
		if (relatedItems != null && relatedItems.size() > 0) {
			
			for (ProductRelationship relationship : relatedItems) {
				relatedProducts.add(relationship.getRelatedProduct());
			}
			
		}

		return relatedProducts;
	}

}
