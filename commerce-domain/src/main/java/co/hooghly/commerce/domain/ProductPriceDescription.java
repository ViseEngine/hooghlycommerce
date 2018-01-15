package co.hooghly.commerce.domain;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name="PRODUCT_PRICE_DESCRIPTION", uniqueConstraints={
		@UniqueConstraint(columnNames={
			"PRODUCT_PRICE_ID",
			"LANGUAGE_ID"
		})
	}
)
@Data
@EqualsAndHashCode(callSuper=true, exclude={"productPrice"})
public class ProductPriceDescription extends Description {
	private static final long serialVersionUID = 270521409645392808L;
	
	public final static String DEFAULT_PRICE_DESCRIPTION = "DEFAULT";
	
	@ManyToOne(targetEntity = ProductPrice.class)
	@JoinColumn(name = "PRODUCT_PRICE_ID", nullable = false)
	private ProductPrice productPrice;
	

}
