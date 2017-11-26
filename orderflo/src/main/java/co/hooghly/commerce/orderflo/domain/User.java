package co.hooghly.commerce.orderflo.domain;

import java.util.Date;

import lombok.Data;

@Data
public class User {

	String email;
	String password;

	Date createdDate;
	Date lastUpdated;
	String firstName;

	int role;
	String middleName;
	String lastName;
	String company;
	String department;
	String apiToken;
}
