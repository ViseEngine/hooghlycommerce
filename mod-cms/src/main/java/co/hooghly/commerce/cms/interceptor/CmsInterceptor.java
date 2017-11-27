package co.hooghly.commerce.cms.interceptor;

import java.util.Optional;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.yaml.snakeyaml.Yaml;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.PageDefinition;
import co.hooghly.commerce.domain.PageDefinitions;
import co.hooghly.commerce.util.ServletUtil;
import lombok.extern.slf4j.Slf4j;

//TODO - Move to CMS MODULE
@Slf4j
public class CmsInterceptor extends HandlerInterceptorAdapter {

	@Value("${commerce.theme}")
	public String theme;

	@Value("classpath*:pagedefs/*-pagedef.yml")
	private Resource[] resources;

	private PageDefinitions pageDefs = new PageDefinitions();

	private final String TITLE = "title";

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {

		if (ServletUtil.isAjaxRequest(request)) {
			if (modelAndView != null) {
				log.info("## Ajax request detected so page definition is not required for view  - {}",
						modelAndView.getViewName());
				modelAndView.setViewName(theme + "/fragments/" + modelAndView.getViewName());
			}
			// == null, returning json
		} else {
			if (modelAndView != null) {
				log.info("## Loading page definition ");
				String viewName = modelAndView.getViewName();
				log.debug("## View Name set in controller - {}", viewName);

				Optional<PageDefinition> pageDef = pageDefs.getDefs().stream().filter(i -> i.getName().equals(viewName))
						.findFirst();

				if (pageDef.isPresent()) {
					MerchantStore store = (MerchantStore) request.getAttribute("MERCHANT_STORE");
					log.debug("## Page defintion not found for viewName - {}", viewName);
					modelAndView.setViewName(store.getId() + "/" + pageDef.get().getLayout());
					modelAndView.addObject("pgDef", pageDef.get());
					modelAndView.addObject(TITLE, pageDef.get().getTitle());
					modelAndView.addObject("theme", theme);
				} else {
					log.debug("## Page defintion not found for viewName - {}", viewName);
					log.info("Please set the page definition correctly. No default is set.");
				}
			}
		}
	}

	@PostConstruct
	public void init() {
		log.info("### Loading page definition information ###");

		for (Resource r : resources) {
			try {
				log.info("#Resource -  {}", r.getFilename());

				Yaml yaml = new Yaml();
				PageDefinitions pgDefs = yaml.loadAs(r.getInputStream(), PageDefinitions.class);

				log.info("#pgDefs -  {}", pgDefs);

				pageDefs.getDefs().addAll(pgDefs.getDefs());

			} catch (Exception e) {
				log.error("Error", e);
			}
		}

		log.info("Page defs - {}", pageDefs);
	}
}
