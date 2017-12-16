package co.hooghly.commerce.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.MessageResourceService;
import co.hooghly.commerce.domain.MessageResource;
import lombok.extern.slf4j.Slf4j;

@Component(value = "messageSource")
@Slf4j
public class DatabaseDrivenMessageSource extends AbstractMessageSource {

	private MessageResourceService messageResourceBusinessDelegate;

	public DatabaseDrivenMessageSource(MessageResourceService messageResourceBusinessDelegate) {
		log.debug("DB backed message source created.");
		this.messageResourceBusinessDelegate = messageResourceBusinessDelegate;

	}

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		log.debug("Code - {} , Locale - {}", code, locale);
		String msg = getText(code, locale);
		log.debug("Retrieved message - {}", msg);
		return createMessageFormat(msg, locale);

	}

	private String getText(String code, Locale locale) {
		String msg = null;
		// first look in the cache

		MessageResource mr = messageResourceBusinessDelegate.findByMessageKeyAndLocale(code, locale);

		if (mr != null) {

			msg = mr.getMessageText();

		}

		return msg;
	}

}