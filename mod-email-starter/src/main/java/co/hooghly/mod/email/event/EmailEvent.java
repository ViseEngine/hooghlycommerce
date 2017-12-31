package co.hooghly.mod.email.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.Assert;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
public class EmailEvent<T> extends ApplicationEvent{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String template;
	private String from;
	private String[] tos;
	private String subject;
	private T data;

	public EmailEvent(Object source, T data, String template, Email email) {
		super(source);
		Assert.isNull(template, "Thymeleaf template mandatory parameter is missing.");
		Assert.isNull(from, "Email from address is required");
		Assert.isNull(tos, "Atleast one to address must be specified.");
		Assert.isNull(subject, "Email subject is mandatory");
		
		this.data = data;
		this.template = template;
		
	}

	

}
