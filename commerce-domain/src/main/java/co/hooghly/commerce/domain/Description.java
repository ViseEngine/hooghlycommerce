package co.hooghly.commerce.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@MappedSuperclass
@EntityListeners(value = AuditListener.class)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public class Description implements Auditable, Serializable {
	private static final long serialVersionUID = -4335863941736710046L;
	
	@Id
	@Column(name = "DESCRIPTION_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Embedded
	private AuditSection auditSection = new AuditSection();
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "LANGUAGE_ID")
	private Language language;
	
	@NotEmpty
	@Column(name="NAME", nullable = false, length=120)
	private String name;
	
	@Column(name="TITLE", length=100)
	private String title;
	
	@Column(name="SUB_TITLE", length=100)
	private String subtitle;
	
	@Column(name="DESCRIPTION")
	@Lob
	private String description;
	
	public Description() {
	}
	
	public Description(Language language, String name) {
		this.setLanguage(language);
		this.setName(name);
	}
	

	
}
