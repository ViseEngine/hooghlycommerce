package co.hooghly.commerce.domain;



import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "LANGUAGE_PACK")
@Cacheable
@Data
@EqualsAndHashCode(callSuper=false)
public class LanguagePack extends SalesManagerEntity<Integer, LanguagePack> implements Auditable {
	private static final long serialVersionUID = -7676627812941330669L;
	

	
	@Id
	@Column(name="LANGUAGE_PACK_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Embedded
	private AuditSection auditSection = new AuditSection();

	@Column(name="LANGUAGE_FILE", nullable = false)
	private String languageFile;
	
	private Language language;
	
	private Country country;
	
}
