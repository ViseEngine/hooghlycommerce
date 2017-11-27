package co.hooghly.commerce.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

@Embeddable
@Data
public class Billing {

	@NotEmpty
	@Column(name = "BILLING_LAST_NAME", length = 64, nullable = false)
	private String lastName;

	@NotEmpty
	@Column(name = "BILLING_FIRST_NAME", length = 64, nullable = false)
	private String firstName;

	@Column(name = "BILLING_COMPANY", length = 100)
	private String company;

	@Column(name = "BILLING_STREET_ADDRESS", length = 256)
	private String address;

	@Column(name = "BILLING_CITY", length = 100)
	private String city;

	@Column(name = "BILLING_POSTCODE", length = 20)
	private String postalCode;

	@Column(name = "BILLING_TELEPHONE", length = 32)
	private String telephone;

	@Column(name = "BILLING_STATE", length = 100)
	private String state;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Country.class)
	@JoinColumn(name = "BILLING_COUNTRY_ID", nullable = false)
	private Country country;

	@ManyToOne(fetch = FetchType.EAGER, targetEntity = Zone.class)
	@JoinColumn(name = "BILLING_ZONE_ID", nullable = true)
	private Zone zone;

}
