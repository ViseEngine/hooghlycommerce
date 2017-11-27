package co.hooghly.mod.email.listener;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import co.hooghly.mod.email.event.EmailEvent;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class SendMailListener<T> {
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Async
	@EventListener
	public void handleMailEvent(EmailEvent<T> event) {
		log.info("A new mail event received." );
		prepareAndSend(event);
	}
	
	private void prepareAndSend(EmailEvent<T> event) {
		
		Context context = new Context();
        context.setVariable("data", event.getData());
		String html = templateEngine.process("mail/" + event.getTemplate(), context);
		
		
		send(event, html);
	}
	
	public void send(EmailEvent<T> event, String html) {
	    MimeMessagePreparator messagePreparator = mimeMessage -> {
	        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
	        messageHelper.setFrom(event.getFrom());
	        messageHelper.setTo(event.getTos());
	        messageHelper.setSubject(event.getSubject());
	        messageHelper.setText(html,true);
	    };
	    try {
	        mailSender.send(messagePreparator);
	    } catch (MailException e) {
	        log.warn("Error sending email - {}", e.getLocalizedMessage());
	    }
	}
}
