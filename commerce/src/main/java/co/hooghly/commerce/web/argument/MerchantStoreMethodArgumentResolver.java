package co.hooghly.commerce.web.argument;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.MerchantStore;

public class MerchantStoreMethodArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(MerchantStore.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		MerchantStore store = (MerchantStore) webRequest.getAttribute(Constants.MERCHANT_STORE, RequestAttributes.SCOPE_REQUEST);
		if(store == null) {
			store = (MerchantStore) webRequest.getAttribute(Constants.ADMIN_STORE, RequestAttributes.SCOPE_REQUEST);
		}
		return store;
		
		
	}

}
