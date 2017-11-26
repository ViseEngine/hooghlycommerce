package co.hooghly.commerce.util;

import co.hooghly.commerce.business.LanguageService;
import static co.hooghly.commerce.constants.Constants.*;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.util.WebUtils;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@Component
@Slf4j
@Deprecated
public class LanguageUtils {

	@Inject
	LanguageService languageService;

	/**
	 * Determines request language based on store rules
	 * 
	 * @param request
	 * @return
	 */
	public Language getRequestLanguage(HttpServletRequest request, HttpServletResponse response) {

		Language language = (Language) WebUtils.getSessionAttribute(request, LANGUAGE);
		// should be browser locale
		Locale locale = LocaleContextHolder.getLocale();
		
		log.info("Determining store view  locale - {}", locale);
		
		if (language == null) {
			try {

				MerchantStore store = (MerchantStore) WebUtils.getSessionAttribute(request, MERCHANT_STORE);
				language = store.getDefaultLanguage(); //language is mandatory for store so if check redundant
				
				locale = languageService.toLocale(language);
				if (locale != null) {
					LocaleContextHolder.setLocale(locale);
				}
				WebUtils.setSessionAttribute(request, LANGUAGE, language);
				

				if (language == null) {
					language = languageService.toLanguage(locale);
					WebUtils.setSessionAttribute(request, LANGUAGE, language);
				}

			} catch (Exception e) {
				if (language == null) {
					try {
						language = languageService.getByCode(DEFAULT_LANGUAGE);
					} catch (Exception ignore) {
					}
				}
			}
		} else {

			if (!language.getCode().equals(locale.getLanguage())) {
				// get locale context
				language = languageService.toLanguage(locale);
			}

		}

		if (language != null) {
			locale = languageService.toLocale(language);
		} else {
			language = languageService.toLanguage(locale);
		}

		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver != null) {
			localeResolver.setLocale(request, response, locale);
		}
		response.setLocale(locale);

		return language;
	}

	/**
	 * Should be used by rest web services
	 * 
	 * @param request
	 * @param store
	 * @return
	 * @throws Exception
	 */
	public Language getRESTLanguage(HttpServletRequest request, MerchantStore store) throws Exception {

		Validate.notNull(request, "HttpServletRequest must not be null");
		Validate.notNull(store, "MerchantStore must not be null");

		Language language = null;

		String lang = request.getParameter(LANG);

		if (StringUtils.isBlank(lang)) {
			// try with HttpSession
			language = (Language) request.getSession().getAttribute(LANGUAGE);
			if (language == null) {
				language = store.getDefaultLanguage();
			}

			if (language == null) {
				language = languageService.defaultLanguage();
			}
		} else {
			language = languageService.getByCode(lang);
			if (language == null) {
				language = (Language) request.getSession().getAttribute(LANGUAGE);
				if (language == null) {
					language = store.getDefaultLanguage();
				}

				if (language == null) {
					language = languageService.defaultLanguage();
				}
			}
		}

		return language;
	}

}
