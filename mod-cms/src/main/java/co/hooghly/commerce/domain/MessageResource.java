package co.hooghly.commerce.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "t_message_resource")
@Data
@EqualsAndHashCode(callSuper = true)
public class MessageResource extends BaseEntity{

	
	@Column(name = "message_key")
	private String messageKey;
	
	@Column(name = "message_text")
	private String messageText;
	
	@Column(name = "locale")
	private String locale;
	
	
	
}