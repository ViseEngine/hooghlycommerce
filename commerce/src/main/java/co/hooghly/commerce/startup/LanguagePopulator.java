package co.hooghly.commerce.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.LanguageService;
import co.hooghly.commerce.domain.Language;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(1)
public class LanguagePopulator extends AbstractDataPopulator {

	public LanguagePopulator() {
		super("LANGUAGE");
	}

	/**
	 * Languages iso codes
	 * 
	 */
	protected static final String[] LANGUAGE_ISO_CODE = { "en", "bn", "ta", "hi" };
	protected static final String[] LANGUAGE_NAME = { "English", "বাংলা", "தமிழ்", "हिंदी" };
	
	@Autowired
	private LanguageService languageService;

	@Override
	public void runInternal(String... args) throws Exception {
		log.debug("1.Loading languages.");
		createLanguages();
	}

	protected void createLanguages()  {
		int counter = 0;
		for (String code : LANGUAGE_ISO_CODE) {
			log.debug("Code - {}", code);
			Language language = new Language();
			language.setCode(code);
			language.setName(LANGUAGE_NAME[counter++]);
			languageService.create(language);
		}
	}
}
