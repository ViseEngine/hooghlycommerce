package co.hooghly.commerce.web.util;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.util.WebUtils;

public class ServletUtils extends ServletRequestUtils {

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

	public static String getCurrentUrl() {
		return getRequest().getRequestURI();
	}

	public static String getPreviousUrl() {
		return getRequest().getHeader("referer");
	}

	public static boolean isRedirectOrForward(String viewName) {
		return viewName.startsWith("redirect:") || viewName.startsWith("forward:");
	}

	public static boolean isParamterNamePresent(String paramName) {
		return getRequest().getParameterMap().containsKey(paramName);
	}

	@SuppressWarnings("unchecked")
	public static <T> T getSessionAttribute(final String key, HttpServletRequest request) {
		return (T) WebUtils.getSessionAttribute(request, key);
	}

	public static void removeSessionAttribute(final String key, HttpServletRequest request) {
		request.getSession().removeAttribute(key);
	}

	public static void setSessionAttribute(final String key, final Object value, HttpServletRequest request) {
		WebUtils.setSessionAttribute(request, key, value);
	}

	private ServletUtils() {
	}
}