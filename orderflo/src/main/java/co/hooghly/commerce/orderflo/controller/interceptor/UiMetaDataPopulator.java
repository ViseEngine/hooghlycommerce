package co.hooghly.commerce.orderflo.controller.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import co.hooghly.commerce.orderflo.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UiMetaDataPopulator extends HandlerInterceptorAdapter {

	/*@Autowired
	private List<MetaInfoPopulatorDelegate> metaInfoLoaderHelpers;*/

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		if (modelAndView == null || !modelAndView.hasView()) {
			log.info("ModelAndView is null or has no view. May be an Ajax request!");
			return;
		}

		String originalViewName = modelAndView.getViewName();

		if (ServletUtil.isRedirectOrForward(originalViewName)) {
			log.info("Redirect or forward request - {}", originalViewName);
			return;
		}

		if (ServletUtil.isAjaxRequest(request)) {
			log.info("Ajax request detected.");
			return;
		}

		/*for (MetaInfoPopulatorDelegate helper : metaInfoLoaderHelpers) {
			try {
				helper.load(request, response, handler, modelAndView);
			} catch (Exception e) {
				log.error("Error", e);
			}
		}*/

	}

}
