package co.hooghly.commerce.domain;

import java.util.*;

import javax.persistence.*;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "MANUFACTURER",  uniqueConstraints=
@UniqueConstraint(columnNames = {"MERCHANT_ID", "CODE"}) )
@Data
@EqualsAndHashCode(callSuper=true)
public class Manufacturer extends AbstractBaseEntity {
	
	
	@Column(name = "MANUFACTURER_IMAGE")
	private String image;
	
	@Column(name="SORT_ORDER")
	private int order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MERCHANT_ID", nullable=false)
	private MerchantStore merchantStore;
	
	@NotEmpty
	@Column(name="CODE", length=100, nullable=false)
	private String code;

	
	@Column(name = "MANUFACTURERS_URL")
	private String url;
	
	@Column(name = "URL_CLICKED")
	private Integer urlClicked;
	
	@Column(name = "DATE_LAST_CLICK")
	private Date dateLastClick;
	
	@NotEmpty
	@Column(name="NAME", nullable = false, length=120)
	private String name;
	
	@Column(name="TITLE", length=100)
	private String title;
	
	@Column(name="SUB_TITLE", length=100)
	private String subtitle;
	
	@Column(name="DESCRIPTION")
	private String description;

}
