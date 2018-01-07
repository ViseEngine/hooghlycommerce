package co.hooghly.commerce.domain;

import javax.persistence.*;



import lombok.Data;
import lombok.EqualsAndHashCode;



@MappedSuperclass
@Data
@EqualsAndHashCode(of={"id"})
public abstract class AbstractBaseEntity implements Auditable{
	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Long id;

	@Transient
	private String _csrf;
	
	@Column(name = "deleted")
	private boolean deleted;

	@Version
	@Column(name = "version")
	private int version;

	
	/*@CreatedDate
	private Date createdDate;
	
	@CreatedBy
	private String createdBy;
	
	@LastModifiedDate  
	private Date lastModifiedDate;
	
	@LastModifiedBy
	private String lastModifiedBy;*/
  
	@Embedded
	private AuditSection auditSection = new AuditSection();
  
	
	
}