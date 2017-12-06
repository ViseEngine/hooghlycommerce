package co.hooghly.commerce.domain;

import java.io.Serializable;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "CURRENCY")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = false)
public class Currency extends SalesManagerEntity<Long, Currency> implements Serializable {
	private static final long serialVersionUID = -999926410367685145L;

	@Id
	@Column(name = "CURRENCY_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "CURRENCY_CURRENCY_CODE", nullable = false, unique = true)
	private java.util.Currency currency;

	@Column(name = "CURRENCY_SUPPORTED")
	private Boolean supported = true;

	@Column(name = "CURRENCY_CODE", unique = true)
	private String code;

	@Column(name = "CURRENCY_NAME", unique = true)
	private String name;

	public void setCurrency(java.util.Currency currency) {
		this.currency = currency;
		this.code = currency.getCurrencyCode();
	}

	public String getCode() {
		if (currency.getCurrencyCode() != code) {
			return currency.getCurrencyCode();
		}
		return code;
	}

}
