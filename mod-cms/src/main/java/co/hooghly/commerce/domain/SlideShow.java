package co.hooghly.commerce.domain;

import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import javax.persistence.Table;
import org.hibernate.validator.constraints.NotEmpty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "SLIDE_SHOW")
@Data
@EqualsAndHashCode(callSuper=true)
public class SlideShow extends AbstractBaseEntity {

	
	@NotEmpty
	@Column(name="CODE", length=100, nullable=false, unique=true)
	private String code;
	
	
	
	
	
	
}