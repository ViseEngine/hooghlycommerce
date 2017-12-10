package co.hooghly.commerce.web.interceptor;

import static co.hooghly.commerce.constants.Constants.*;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.util.WebUtils;

import co.hooghly.commerce.business.MerchantStoreService;
import co.hooghly.commerce.business.MerchantStoreViewService;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.MerchantStoreView;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(1)
public class MerchantStoreProcessingStrategy implements WebInterceptorProcessingStrategy{
	
	private static final String STORE_VIEW_REQUEST_PARAMETER = "storeView";
	
	@Autowired
	private MerchantStoreService merchantService;
	
	@Autowired
	private MerchantStoreViewService merchantStoreViewService;

	@Override
	public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
		log.info("Processing merchant store and view");
		try {
			findAndSetMerchantStoreView(request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void postHandle() {
		
		
	}

	@Override
	public boolean canHandle(String clazz) {
		return StringUtils.equals(clazz, "StoreInterceptor");
		
	}
	
	protected MerchantStoreView findAndSetMerchantStoreView(HttpServletRequest request) throws Exception {
		/** merchant store **/
		MerchantStoreView storeView = (MerchantStoreView) WebUtils.getSessionAttribute(request, MERCHANT_STORE_VIEW);
		String storeViewCode = ServletRequestUtils.getStringParameter(request, STORE_VIEW_REQUEST_PARAMETER);

		if (StringUtils.isNotBlank(storeViewCode) && storeView != null) {
			// A store code found in request and session so handle the
			// conflict by
			// trying to use the request param store code.
			// override the session store code with request store code.
			// the user might have requested a change by selecting language or
			// currency
			log.info("Store view changed");
			storeView = setMerchantStoreViewInSession(request, storeViewCode);
		}

		if (storeView == null) {
			// merchant store not found in session or override did not work, set
			// default - this is for first time use
			storeView = setMerchantStoreViewInSession(request, null);
			// set default store view .

		}
		request.setAttribute(MERCHANT_STORE, storeView.getMerchantStore());
		request.setAttribute(MERCHANT_STORE_VIEW, storeView);

		return storeView;
	}

	private MerchantStoreView setMerchantStoreViewInSession(HttpServletRequest request, String storeViewCode) {
		MerchantStoreView view = null;
		MerchantStore store = null;
		Optional<MerchantStoreView> mView = Optional.empty();
		if (StringUtils.isNotBlank(storeViewCode)) {
			log.info("Retrieve storeView - {}", storeViewCode);
			mView = merchantStoreViewService.findByCode(storeViewCode);

		} else {
			// use default merchant store , first time request
			store = merchantService.getByCode(MerchantStore.DEFAULT_STORE);
			mView = store.getStoreViews().stream().filter(i -> i.isDefaultView()).findFirst();
		}

		if (mView.isPresent()) {
			view = mView.get();
			request.getSession().setAttribute(MERCHANT_STORE, view.getMerchantStore());
			request.getSession().setAttribute(MERCHANT_STORE_VIEW, view);
		}

		return view;
	}
}
