package co.hooghly.commerce.domain;

import java.util.Locale;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "LANGUAGE")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = true)
public class Language extends AbstractBaseEntity {

	@Column(name = "CODE", nullable = false)
	private String code;

	@Column(name = "LANGUAGE_NAME", nullable = false)
	private String name;

	@Column(name = "SORT_ORDER")
	private Integer sortOrder;

	public Locale computeLocale() {
		return new Locale(code);
	}

	public Locale computeLocale(Country country) {
		
		return new Locale(code, country.getIsoCode());
	}
}
