package co.hooghly.commerce.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;

public interface WebInterceptorProcessingStrategy {
	
	public boolean canHandle(String clazz);
	public void preHandle(HttpServletRequest request, HttpServletResponse response, Object handler);
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView);
}
