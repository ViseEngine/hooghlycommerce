package co.hooghly.commerce.shop.controller;


import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.web.ui.PageInformation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Controller
@ConditionalOnProperty(prefix="shop.controller.ShopContentController", name="enabled")
public class ShopContentController {
	
	
	

	
	@RequestMapping("/shop/pages/{friendlyUrl}.html")
	public String displayContent(@PathVariable final String friendlyUrl, Model model, HttpServletRequest request, HttpServletResponse response, Locale locale) throws Exception {
		
		MerchantStore store = (MerchantStore)request.getAttribute(Constants.MERCHANT_STORE);

		/*ContentDescription contentDescription = contentService.getBySeUrl(store, friendlyUrl);
		
		Content content = null;
		
		if(contentDescription!=null) {
			
			content = contentDescription.getContent();
			
			if(!content.isVisible()) {
				return "redirect:/shop";
			}
			
			//meta information
			PageInformation pageInformation = new PageInformation();
			pageInformation.setPageDescription(contentDescription.getMetatagDescription());
			pageInformation.setPageKeywords(contentDescription.getMetatagKeywords());
			pageInformation.setPageTitle(contentDescription.getTitle());
			pageInformation.setPageUrl(contentDescription.getName());
			
			request.setAttribute(Constants.REQUEST_PAGE_INFORMATION, pageInformation);
			
			
			
			
		}
		
		//TODO breadcrumbs
		request.setAttribute(Constants.LINK_CODE, contentDescription.getSeUrl());
		model.addAttribute("content",contentDescription);

		if(!StringUtils.isBlank(content.getProductGroup())) {
			model.addAttribute("productGroup",content.getProductGroup());
		}
		*/
		/** template **/
		StringBuilder template = new StringBuilder().append(ControllerConstants.Tiles.Content.content).append(".").append(store.getStoreTemplate());

		return template.toString();
		
		
	}
	
}