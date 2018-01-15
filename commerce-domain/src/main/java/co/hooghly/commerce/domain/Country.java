package co.hooghly.commerce.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
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

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@Table(name = "COUNTRY")
@Cacheable
@Data
@EqualsAndHashCode(callSuper = true)
public class Country extends SalesManagerEntity<Long, Country> {
	private static final long serialVersionUID = -7388011537255588035L;

	@Id
	@Column(name="COUNTRY_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToMany(mappedBy = "country", cascade = CascadeType.ALL)
	private List<CountryDescription> descriptions = new ArrayList<>();
	
	@OneToMany(mappedBy = "country")
	private List<Zone> zones = new ArrayList<Zone>();
	
	@ManyToOne(targetEntity = GeoZone.class)
	@JoinColumn(name = "GEOZONE_ID")
	private GeoZone geoZone;
	
	@Column(name = "COUNTRY_SUPPORTED")
	private boolean supported = true;
	
	@Column(name = "COUNTRY_ISOCODE", unique=true, nullable = false)
	private String isoCode;
	
	@Transient
	private String name;
	
	

	public Country() {
	}
	
	public Country(String isoCode) {
		this.setIsoCode(isoCode);
	}
	
	
}
