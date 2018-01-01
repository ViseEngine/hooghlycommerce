package co.hooghly.commerce.orderflo.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
@Table(name = "t_user_roles")
@Data
@EqualsAndHashCode
@Order(0)
public class UserRole implements Serializable {
	
	@Id
	@Column(name = "user_id",nullable=false)
	String user_id;
	
	@Column(name = "role_id" ,nullable=false)
	int role_id;
	
}
