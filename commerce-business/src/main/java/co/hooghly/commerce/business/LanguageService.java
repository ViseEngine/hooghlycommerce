package co.hooghly.commerce.business;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.business.utils.CacheUtils;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.repository.LanguageRepository;

@Service
public class LanguageService extends SalesManagerEntityServiceImpl<Integer, Language> {

	private static final Logger LOGGER = LoggerFactory.getLogger(LanguageService.class);

	@Autowired
	private CacheUtils cache;

	private LanguageRepository languageRepository;

	public LanguageService(LanguageRepository languageRepository) {
		super(languageRepository);
		this.languageRepository = languageRepository;
	}

	public Language getByCode(String code) throws ServiceException {
		return languageRepository.findByCode(code);
	}

	public Locale toLocale(Language language) {
		return new Locale(language.getCode());
	}

	public Language toLanguage(Locale locale) {

		Language lang = getLanguagesMap().get(locale.getLanguage());
		if (lang == null) {
			lang = new Language();
			lang.setCode(Constants.DEFAULT_LANGUAGE);
		}
		return lang;

	}

	public Map<String, Language> getLanguagesMap() {

		List<Language> langs = this.getLanguages();
		Map<String, Language> returnMap = new LinkedHashMap<String, Language>();

		for (Language lang : langs) {
			returnMap.put(lang.getCode(), lang);
		}
		return returnMap;

	}

	@SuppressWarnings("unchecked")
	public List<Language> getLanguages() {

		List<Language> langs = null;
		try {

			langs = (List<Language>) cache.getFromCache("LANGUAGES");
			if (langs == null) {
				langs = this.list();
				cache.putInCache(langs, "LANGUAGES");
			}

		} catch (Exception e) {
			LOGGER.error("getCountries()", e);
			throw new ServiceException(e);
		}

		return langs;

	}

	public Language defaultLanguage() {
		return toLanguage(Locale.ENGLISH);
	}

}
