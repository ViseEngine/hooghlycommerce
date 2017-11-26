package co.hooghly.commerce.domain;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "PRODUCT_TYPE")
public class ProductType extends SalesManagerEntity<Long, ProductType> implements Auditable {
	private static final long serialVersionUID = 65541494628227593L;
	
	public final static String GENERAL_TYPE = "GENERAL";
	
	@Id
	@Column(name = "PRODUCT_TYPE_ID", unique=true, nullable=false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Embedded
	private AuditSection auditSection = new AuditSection();
	
	@Column(name = "PRD_TYPE_CODE")
	private String code;
	
	@Column(name = "PRD_TYPE_ADD_TO_CART")
	private Boolean allowAddToCart;
	
	public ProductType() {
	}
	
	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public AuditSection getAuditSection() {
		return auditSection;
	}

	@Override
	public void setAuditSection(AuditSection auditSection) {
		this.auditSection = auditSection;
	}

	public boolean isAllowAddToCart() {
		return allowAddToCart;
	}

	public void setAllowAddToCart(boolean allowAddToCart) {
		this.allowAddToCart = allowAddToCart;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}


}
