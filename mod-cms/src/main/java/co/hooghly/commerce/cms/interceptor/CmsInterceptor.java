package co.hooghly.commerce.cms.interceptor;

import java.util.Optional;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import co.hooghly.commerce.business.PageDefinitionService;
import co.hooghly.commerce.domain.MerchantStoreView;
import co.hooghly.commerce.domain.PageDefinition;
import co.hooghly.commerce.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CmsInterceptor extends HandlerInterceptorAdapter {
	
	@Autowired
	private PageDefinitionService pageDefinitionService;


	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		if (ServletUtil.isAjaxRequest(request)) {
			if (modelAndView != null) {
				// == null, returning json
				log.debug("## Ajax request detected so page definition is not required for view  - {}",
						modelAndView.getViewName());
				modelAndView.setViewName("/fragments/" + modelAndView.getViewName());
			}
			
		} else {
			if (modelAndView != null) {
				String viewName = modelAndView.getViewName();
				log.info("Loading page definition. View Name set in controller - {}", viewName);

				Optional<PageDefinition> pageDef = pageDefinitionService.findByName(viewName);

				if (pageDef.isPresent()) {
					log.debug("Page defintion found for viewName - {}", viewName);
					MerchantStoreView storeView = (MerchantStoreView) request.getAttribute("MERCHANT_STORE_VIEW");
					
					modelAndView.setViewName(storeView.getMerchantStore().getId() + "/" + pageDef.get().getLayout() + ".cms");
					modelAndView.addObject("pgDef", pageDef.get());
					
					modelAndView.addObject("theme", storeView.getTheme());
				} else {
					log.warn("## Page defintion not found for viewName - {}. Please set the page definition correctly. No default is set.", viewName);
					
				}
			}
		}
	}
	
}
