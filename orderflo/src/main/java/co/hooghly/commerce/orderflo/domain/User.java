package co.hooghly.commerce.orderflo.domain;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.springframework.core.annotation.Order;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "t_user")
@Data
@EqualsAndHashCode
@Order(1)
public class User {
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	@Column(name = "id", length = 40)
	private String id;
	
	//@Id
	@Column(name = "email", unique = true)
	String email;
	
	@Column(name = "password")
	String password;

	@Column(name = "createdDate")
	Date createdDate;
	@Column(name = "lastUpdated")
	Date lastUpdated;
	@Column(name = "firstName")
	String firstName;
	
	@Column(name = "role")
	String role;

	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER)
	@JoinTable(name = "t_user_roles", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id", referencedColumnName = "id", unique = true) }			
			)
	private List<Role> roles;
	

	@Column(name = "middleName")
	String middleName;
	@Column(name = "lastName")
	String lastName;
	@Column(name = "company")
	String company;
	@Column(name = "department")
	String department;
	@Column(name = "apiToken")
	String apiToken;
}
