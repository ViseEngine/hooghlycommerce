package co.hooghly.commerce.business;

import java.util.Locale;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.domain.MessageResource;
import co.hooghly.commerce.repository.MessageResourceRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageResourceService extends AbstractBaseBusinessDelegate<MessageResource, String>{
	private MessageResourceRepository messageResourceRepository;
	
	public MessageResourceService(MessageResourceRepository repository) {
		super(repository);
		this.messageResourceRepository = repository;
	}
	
	@Cacheable("msg-key-locale")
	public MessageResource findByMessageKeyAndLocale(String messageKey, Locale locale) {
		MessageResource mr = null;
		try {
			mr = messageResourceRepository.findByMessageKeyAndLocale(messageKey, locale + "");
		}
		catch(Exception e) {
			log.warn("Error retrieving msg key - {} , error : {}", messageKey, e.getLocalizedMessage());
		}
		
		if(mr == null){
			mr = new MessageResource();
			mr.setMessageKey(messageKey);
			mr.setMessageText(messageKey);
			mr.setLocale(locale + "");
		}
		
		return mr;
	}
	
}