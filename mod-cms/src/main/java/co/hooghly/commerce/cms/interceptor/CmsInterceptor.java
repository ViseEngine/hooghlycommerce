package co.hooghly.commerce.cms.interceptor;

import static co.hooghly.commerce.constants.Constants.MERCHANT_STORE;

import java.util.Optional;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;

import co.hooghly.commerce.business.PageDefinitionService;
import co.hooghly.commerce.domain.MerchantStore;
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
				log.debug("Loading page definition. View Name set in controller - {}", viewName);

				Optional<PageDefinition> pageDef = pageDefinitionService.findByName(viewName);

				if (pageDef.isPresent()) {
					log.debug("Page defintion found for viewName - {}", viewName);
					MerchantStore store = (MerchantStore) WebUtils.getSessionAttribute(request, MERCHANT_STORE);
					
					modelAndView.setViewName(store.getId() + "/" + pageDef.get().getLayout() + ".cms");
					modelAndView.addObject("pgDef", pageDef.get());
					
					log.info("Page title - {}", pageDef.get().getTitle());
					
					modelAndView.addObject("theme", store.getTheme());
				} else {
					log.warn("## Page defintion not found for viewName - {}. Please set the page definition correctly. No default is set.", viewName);
					
				}
			}
		}
	}
	
}
