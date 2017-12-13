package co.hooghly.commerce.shop.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.ProductRelationshipService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductRelationship;
import co.hooghly.commerce.domain.ProductRelationshipType;
import co.hooghly.commerce.util.ImageFilePath;
import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.web.ui.Breadcrumb;
import co.hooghly.commerce.web.ui.BreadcrumbItem;
import co.hooghly.commerce.web.ui.BreadcrumbItemType;
import co.hooghly.commerce.web.ui.ReadableProduct;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/shop")
@Slf4j
@ConditionalOnProperty(prefix="hooghly.shop.controller", name="enabled")
public class ShopHomeController {

	private static final String LANDING_PAGE = "LANDING_PAGE";
	private static final String HOME_LINK_CODE = "HOME";

	@Inject
	private ProductRelationshipService productRelationshipService;

	@Inject
	private LabelUtils messages;

	@Inject
	private PricingService pricingService;

	@Inject
	private MerchantStoreService merchantService;

	// @Inject
	// @Qualifier("img")
	private ImageFilePath imageUtils;

	@GetMapping("")
	public String displayLanding(MerchantStore store, Language language , Model model, HttpServletRequest request,
			HttpServletResponse response, Locale locale)  {
		log.info("Shop landing page for storeveiew - {}", store.getCode());

		

		request.setAttribute(Constants.LINK_CODE, HOME_LINK_CODE);

		

		/** Rebuild breadcrumb **/
		BreadcrumbItem item = new BreadcrumbItem();
		item.setItemType(BreadcrumbItemType.HOME);
		item.setLabel(messages.getMessage(Constants.HOME_MENU_KEY, locale));
		item.setUrl(Constants.HOME_URL);

		Breadcrumb breadCrumb = new Breadcrumb();
		breadCrumb.setLanguage(language);

		List<BreadcrumbItem> items = new ArrayList<BreadcrumbItem>();
		items.add(item);

		breadCrumb.setBreadCrumbs(items);
		request.getSession().setAttribute(Constants.BREADCRUMB, breadCrumb);
		request.setAttribute(Constants.BREADCRUMB, breadCrumb);
		

		
		return "landing";
	}

	@GetMapping("/shop/featured")
	public String findFeaturedProducts(@RequestParam("fragment") String fragment, HttpServletRequest request,
			Model model) throws Exception {
		log.info("FRAGMENT  - {}", fragment);
		Language language = (Language) request.getAttribute(Constants.LANGUAGE);

		MerchantStore store = (MerchantStore) request.getAttribute(Constants.MERCHANT_STORE);

		// featured items
		List<ProductRelationship> relationships = productRelationshipService.getByType(store,
				ProductRelationshipType.FEATURED_ITEM, language);
		List<ReadableProduct> featuredItems = new ArrayList<ReadableProduct>();
		List<Product> items = new ArrayList<Product>();
		for (ProductRelationship relationship : relationships) {

			Product product = relationship.getRelatedProduct();

			items.add(product);
		}

		log.info("featuredItems  - {}", items.size());
		model.addAttribute("featuredItems", items);

		return fragment;

	}

	@RequestMapping(value = { Constants.SHOP_URI + "/stub.html" }, method = RequestMethod.GET)
	public String displayHomeStub(Model model, HttpServletRequest request, HttpServletResponse response, Locale locale)
			throws Exception {
		return "index";
	}

	@RequestMapping(value = Constants.SHOP_URI + "/home/{store}", method = RequestMethod.GET)
	public String displayStoreLanding(@PathVariable final String store, HttpServletRequest request,
			HttpServletResponse response) {

		try {

			request.getSession().invalidate();
			request.getSession().removeAttribute(Constants.MERCHANT_STORE);

			MerchantStore merchantStore = merchantService.getByCode(store);
			if (merchantStore != null) {
				request.getSession().setAttribute(Constants.MERCHANT_STORE, merchantStore);
			} else {
				log.error("MerchantStore does not exist for store code " + store);
			}

		} catch (Exception e) {
			log.error("Error occured while getting store code " + store, e);
		}

		return "redirect:" + Constants.SHOP_URI;
	}

}
