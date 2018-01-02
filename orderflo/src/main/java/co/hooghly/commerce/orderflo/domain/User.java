package co.hooghly.commerce.orderflo.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	
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
	
	@OneToMany(cascade = CascadeType.MERGE,fetch = FetchType.EAGER)
	@JoinTable(name = "t_user_roles", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
			@JoinColumn(name = "role_id", referencedColumnName = "id", unique = true) }			
			)
	private List<Role> roles = new ArrayList<>();
	

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
