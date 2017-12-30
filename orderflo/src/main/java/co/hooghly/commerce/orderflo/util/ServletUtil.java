package co.hooghly.commerce.orderflo.util;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ServletUtil {

	public static boolean isAjaxRequest(HttpServletRequest webRequest) {
		String requestedWith = webRequest.getHeader("X-Requested-With");
		return requestedWith != null ? "XMLHttpRequest".equals(requestedWith) : false;
	}

	public static boolean isAjaxUploadRequest(HttpServletRequest webRequest) {
		return webRequest.getParameter("ajaxUpload") != null;
	}

	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	}

	public static String getParamVal(String param) {
		return getRequest().getParameter(param);
	}

	public static String getCurrentUrl() {
		return getRequest().getRequestURI();
	}

	public static String getPreviousUrl() {
		return getRequest().getHeader("referer");
	}

	public static boolean isRedirectOrForward(String viewName) {
		return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
	}

	public static boolean isEditRequest() {
		return getCurrentUrl().contains("edit");
	}

	public static boolean isRecycleRequest() {
		return getCurrentUrl().contains("recycle");
	}
	
	public static boolean isParamterNamePresent(String paramName) {
		return getRequest().getParameterMap().containsKey(paramName);
	}

	private ServletUtil() {
	}
}