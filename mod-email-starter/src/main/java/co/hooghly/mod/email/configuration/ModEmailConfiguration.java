package co.hooghly.mod.email.configuration;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import co.hooghly.mod.email.listener.SendMailListener;
import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class ModEmailConfiguration {
	
	
	
	@Bean
	@ConditionalOnProperty(name = "spring.mail.host")
	@ConditionalOnMissingBean
	public SendMailListener sendMailListener(@Autowired JavaMailSender javaMailSener) {
		log.debug("## Send mail listener configuration");
		
		return BeanUtils.instantiate(SendMailListener.class);
	}
}
