package co.hooghly.commerce.domain;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

@MappedSuperclass
@Data
@EqualsAndHashCode(of={"id"})
public abstract class BaseEntity {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", length = 40)
	private String id;

	@Transient
	private String _csrf;
	
	@Column(name = "deleted")
	private boolean deleted;

	@Version
	@Column(name = "version")
	private int version;

	
	@CreatedDate
	private Date createdDate;
	
	@CreatedBy
	private String createdBy;
	
	@LastModifiedDate  
	private Date lastModifiedDate;
	
	@LastModifiedBy
	private String lastModifiedBy;
  
	
	
}