package co.hooghly.commerce.shop.controller;

import co.hooghly.commerce.business.CustomerService;
import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.business.PricingService;
import co.hooghly.commerce.business.ProductReviewService;
import co.hooghly.commerce.business.ProductService;
import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Product;
import co.hooghly.commerce.domain.ProductReview;
import co.hooghly.commerce.facade.CustomerFacade;
import co.hooghly.commerce.util.DateUtil;
import co.hooghly.commerce.util.ImageFilePath;
import co.hooghly.commerce.util.LabelUtils;
import co.hooghly.commerce.web.populator.PersistableProductReviewPopulator;
import co.hooghly.commerce.web.populator.ReadableProductPopulator;
import co.hooghly.commerce.web.populator.ReadableProductReviewPopulator;
import co.hooghly.commerce.web.ui.PersistableProductReview;
import co.hooghly.commerce.web.ui.ReadableProduct;
import co.hooghly.commerce.web.ui.ReadableProductReview;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Entry point for logged in customers
 *
 */
@Controller
@RequestMapping(Constants.SHOP_URI + "/customer")
@ConditionalOnProperty(prefix="shop.controller.CustomerProductReviewController", name="enabled")
public class CustomerProductReviewController extends AbstractController {
	
	@Inject
	private ProductService productService;
	
	@Inject
	private LanguageService languageService;
	
	@Inject
	private PricingService pricingService;
	
	@Inject
	private ProductReviewService productReviewService;
	
	@Inject
	private CustomerService customerService;
	
	@Inject
	private CustomerFacade customerFacade;
	
	@Inject
	private LabelUtils messages;
	
	//@Inject
	//@Qualifier("img")
	private ImageFilePath imageUtils;

	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/review.html", method=RequestMethod.GET)
	public String displayProductReview(@RequestParam Long productId, Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
	    Language language = super.getLanguage(request);

        
        
        //get product
        Product product = productService.getById(productId);
        if(product==null) {
        	return "redirect:" + Constants.SHOP_URI;
        }
        
        if(product.getMerchantStore().getId().intValue()!=store.getId().intValue()) {
        	return "redirect:" + Constants.SHOP_URI;
        }
        
        
        //create readable product
        ReadableProduct readableProduct = new ReadableProduct();
        ReadableProductPopulator readableProductPopulator = new ReadableProductPopulator();
        readableProductPopulator.setPricingService(pricingService);
        readableProductPopulator.setimageUtils(imageUtils);
        readableProductPopulator.populate(product, readableProduct,  store, language);
        model.addAttribute("product", readableProduct);
        

        Customer customer =  customerFacade.getCustomerByUserName(request.getRemoteUser(), store);
        
        List<ProductReview> reviews = productReviewService.getByProduct(product, language);
	    for(ProductReview r : reviews) {
	    	if(r.getCustomer().getId().longValue()==customer.getId().longValue()) {
	    		
				ReadableProductReviewPopulator reviewPopulator = new ReadableProductReviewPopulator();
				ReadableProductReview rev = new ReadableProductReview();
				reviewPopulator.populate(r, rev, store, language);
	    		
	    		model.addAttribute("customerReview", rev);
	    		break;
	    	}
	    }
        
        
        ProductReview review = new ProductReview();
        review.setCustomer(customer);
        review.setProduct(product);
        
        ReadableProductReview productReview = new ReadableProductReview();
        ReadableProductReviewPopulator reviewPopulator = new ReadableProductReviewPopulator();
        reviewPopulator.populate(review,  productReview, store, language);
        
        model.addAttribute("review", productReview);
        model.addAttribute("reviews", reviews);
		
		
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.review).append(".").append(store.getStoreTemplate());

		return template.toString();
		
	}
	
	
	@PreAuthorize("hasRole('AUTH_CUSTOMER')")
	@RequestMapping(value="/review/submit.html", method=RequestMethod.POST)
	public String submitProductReview(@ModelAttribute("review") PersistableProductReview review, BindingResult bindingResult, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		

	    MerchantStore store = getSessionAttribute(Constants.MERCHANT_STORE, request);
	    Language language = getLanguage(request);
	    
        Customer customer =  customerFacade.getCustomerByUserName(request.getRemoteUser(), store);
        
        if(customer==null) {
        	return "redirect:" + Constants.SHOP_URI;
        }

	    
	    Product product = productService.getById(review.getProductId());
	    if(product==null) {
	    	return "redirect:" + Constants.SHOP_URI;
	    }
	    
	    if(StringUtils.isBlank(review.getDescription())) {
	    	FieldError error = new FieldError("description","description",messages.getMessage("NotEmpty.review.description", locale));
			bindingResult.addError(error);
	    }
	    

	    
        ReadableProduct readableProduct = new ReadableProduct();
        ReadableProductPopulator readableProductPopulator = new ReadableProductPopulator();
        readableProductPopulator.setPricingService(pricingService);
        readableProductPopulator.setimageUtils(imageUtils);
        readableProductPopulator.populate(product, readableProduct,  store, language);
        model.addAttribute("product", readableProduct);
	    

		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Customer.review).append(".").append(store.getStoreTemplate());

        if ( bindingResult.hasErrors() )
        {

            return template.toString();

        }
		
        
        //check if customer has already evaluated the product
	    List<ProductReview> reviews = productReviewService.getByProduct(product);
	    
	    for(ProductReview r : reviews) {
	    	if(r.getCustomer().getId().longValue()==customer.getId().longValue()) {
				ReadableProductReviewPopulator reviewPopulator = new ReadableProductReviewPopulator();
				ReadableProductReview rev = new ReadableProductReview();
				reviewPopulator.populate(r, rev, store, language);
	    		
	    		model.addAttribute("customerReview", rev);
	    		return template.toString();
	    	}
	    }

	    
	    PersistableProductReviewPopulator populator = new PersistableProductReviewPopulator();
	    populator.setCustomerService(customerService);
	    populator.setLanguageService(languageService);
	    populator.setProductService(productService);
	    
	    review.setDate(DateUtil.formatDate(new Date()));
	    review.setCustomerId(customer.getId());
	    
	    ProductReview productReview = populator.populate(review, store, language);
	    productReviewService.create(productReview);
        
        model.addAttribute("review", review);
        model.addAttribute("success", "success");
        
		ReadableProductReviewPopulator reviewPopulator = new ReadableProductReviewPopulator();
		ReadableProductReview rev = new ReadableProductReview();
		reviewPopulator.populate(productReview, rev, store, language);
		
        model.addAttribute("customerReview", rev);

		return template.toString();
		
	}
	
	
	
}
