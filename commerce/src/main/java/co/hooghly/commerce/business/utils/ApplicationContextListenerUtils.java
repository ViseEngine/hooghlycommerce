package co.hooghly.commerce.business.utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStartedEvent;

import co.hooghly.commerce.business.SearchService;

public class ApplicationContextListenerUtils implements ApplicationListener<ContextStartedEvent> {

	@Override
	public void onApplicationEvent(ContextStartedEvent event) {
		 ApplicationContext applicationContext = event.getApplicationContext();
		 /** init search service **/
		 SearchService searchService = (SearchService)applicationContext.getBean("productSearchService");
		 searchService.initService();
		
	}

}
