package co.hooghly.commerce.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;



/**
 * Optin defines optin campaigns for the system.
 * 
 *
 */
@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "CUSTMER_OPTIN",uniqueConstraints=
@UniqueConstraint(columnNames = {"EMAIL", "OPTIN_ID"}))
public class CustomerOptin extends SalesManagerEntity<Long, CustomerOptin> implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "CUSTOMER_OPTIN_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column (name ="OPTIN_DATE")
	private Date optinDate;

	
	@ManyToOne(targetEntity = Optin.class)
	@JoinColumn(name="OPTIN_ID")
	private Optin optin;
	
	@Column(name="FIRST")
	private String firstName;
	
	@Column(name="LAST")
	private String lastName;
	
	@Column(name="EMAIL")
	private String email;
	
	@Column(name="VALUE")
	@Lob
	private String value;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;	
	}

	public Date getOptinDate() {
		return optinDate;
	}

	public void setOptinDate(Date optinDate) {
		this.optinDate = optinDate;
	}

	public Optin getOptin() {
		return optin;
	}

	public void setOptin(Optin optin) {
		this.optin = optin;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
