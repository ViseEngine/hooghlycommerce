package co.hooghly.commerce.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "PRODUCT_TYPE")
@Data
@EqualsAndHashCode(callSuper=true)
public class ProductType extends AbstractBaseEntity {

	public static final String GENERAL_TYPE = "GENERAL";

	

	@Column(name = "PRD_TYPE_CODE")
	private String code;

	@Column(name = "PRD_TYPE_ADD_TO_CART")
	private Boolean allowAddToCart;



}
