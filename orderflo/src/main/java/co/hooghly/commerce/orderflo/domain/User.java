package co.hooghly.commerce.orderflo.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "t_usersss")
@Data
@EqualsAndHashCode
public class User {
	
	@Id
	@Column(name = "email")
	String email;
	@Column(name = "password")
	String password;

	@Column(name = "createdDate")
	Date createdDate;
	@Column(name = "lastUpdated")
	Date lastUpdated;
	@Column(name = "firstName")
	String firstName;

//	int role;
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
