package co.hooghly.commerce.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name = "ZONE")
public class Zone extends SalesManagerEntity<Long, Zone>{
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ZONE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(mappedBy = "zone", cascade = CascadeType.ALL)
	private List<ZoneDescription> descriptions = new ArrayList<ZoneDescription>();
	
	@ManyToOne
	@JoinColumn(name="COUNTRY_ID", nullable = false)
	private Country country;
	
	@Transient
	private String name;
	

	
	@Column(name = "ZONE_CODE", nullable = false)
	private String code;
	
	public Zone() {
	}
	
	public Zone(Country country, String name, String code) {
		this.setCode(code);
		this.setCountry(country);
		this.setCode(name);
	}
	
	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}



	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}
	public List<ZoneDescription> getDescriptions() {
		return descriptions;
	}

	public void setDescriptons(List<ZoneDescription> descriptions) {
		this.descriptions = descriptions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
