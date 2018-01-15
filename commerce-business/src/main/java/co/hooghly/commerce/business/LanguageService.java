package co.hooghly.commerce.business;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.hooghly.commerce.constants.Constants;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.repository.LanguageRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class LanguageService extends AbstractBaseBusinessDelegate<Language, Long> {

	private LanguageRepository languageRepository;

	public LanguageService(LanguageRepository languageRepository) {
		super(languageRepository);
		this.languageRepository = languageRepository;
	}

	public Language getByCode(String code) {
		return languageRepository.findByCode(code);
	}
	
	@Deprecated
	@Transactional(readOnly=true)
	public Locale toLocale(Language language) {
		return new Locale(language.getCode());
	}
	
	@Transactional(readOnly=true)
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

	public List<Language> getLanguages() {

		return languageRepository.findAll();

	}

	public Language defaultLanguage() {
		return toLanguage(Locale.ENGLISH);
	}

}
