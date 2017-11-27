package co.hooghly.commerce.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import org.hibernate.annotations.Cascade;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "CUSTOMER")
@Data
@EqualsAndHashCode(callSuper = false)
public class Customer extends SalesManagerEntity<Long, Customer> {
	private static final long serialVersionUID = -6966934116557219193L;

	@Id
	@Column(name = "CUSTOMER_ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "customer")
	private Set<CustomerAttribute> attributes = new HashSet<CustomerAttribute>();

	@Column(name = "CUSTOMER_GENDER", length = 1, nullable = true)
	@Enumerated(value = EnumType.STRING)
	private CustomerGender gender;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CUSTOMER_DOB")
	private Date dateOfBirth;

	@Email
	@NotEmpty
	@Column(name = "CUSTOMER_EMAIL_ADDRESS", length = 96, nullable = false)
	private String emailAddress;

	@Column(name = "CUSTOMER_NICK", length = 96)
	private String nick;

	@Column(name = "CUSTOMER_COMPANY", length = 100)
	private String company;

	@Column(name = "CUSTOMER_PASSWORD", length = 60)
	private String password;

	@Column(name = "CUSTOMER_ANONYMOUS")
	private boolean anonymous;

	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Language.class)
	@JoinColumn(name = "LANGUAGE_ID", nullable = false)
	private Language defaultLanguage;

	@OneToMany(mappedBy = "customer", targetEntity = ProductReview.class)
	private List<ProductReview> reviews = new ArrayList<ProductReview>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MERCHANT_ID", nullable = false)
	private MerchantStore merchantStore;

	@Embedded
	private Delivery delivery;

	@Valid
	@Embedded
	private Billing billing;

	@ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.REFRESH })
	@JoinTable(name = "CUSTOMER_GROUP", joinColumns = {
			@JoinColumn(name = "CUSTOMER_ID", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "GROUP_ID", nullable = false, updatable = false) })
	@Cascade({ org.hibernate.annotations.CascadeType.DETACH, org.hibernate.annotations.CascadeType.LOCK,
			org.hibernate.annotations.CascadeType.REFRESH, org.hibernate.annotations.CascadeType.REPLICATE

	})
	private List<Group> groups = new ArrayList<>();

	@Transient
	private String showCustomerStateList;

	@Transient
	private String showBillingStateList;

	@Transient
	private String showDeliveryStateList;

	public Date getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public static Customer getInstance(MerchantStore store) {
		Customer customer = new Customer();
		Billing billing = new Billing();
		billing.setCountry(store.getCountry());
		billing.setZone(store.getZone());
		billing.setState(store.getStorestateprovince());
		/** empty postal code for initial quote **/
		// billing.setPostalCode(store.getStorepostalcode());
		customer.setBilling(billing);

		Delivery delivery = new Delivery();
		delivery.setCountry(store.getCountry());
		delivery.setZone(store.getZone());
		delivery.setState(store.getStorestateprovince());
		/** empty postal code for initial quote **/
		// delivery.setPostalCode(store.getStorepostalcode());
		customer.setDelivery(delivery);

		return customer;
	}

}
