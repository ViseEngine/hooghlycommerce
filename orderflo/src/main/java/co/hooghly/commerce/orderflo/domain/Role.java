package co.hooghly.commerce.orderflo.domain;

import javax.persistence.*;

import lombok.Data;

@Entity
@Table(name="t_role")
@Data
public class Role {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Basic(optional=false)
	private String name;
}
