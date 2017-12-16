package co.hooghly.commerce.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "CATEGORY", uniqueConstraints = @UniqueConstraint(columnNames = { "MERCHANT_ID", "CODE" }))
@Data
@EqualsAndHashCode(callSuper = true)
public class Category extends AbstractBaseEntity  {
	

	

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "MERCHANT_ID", nullable = false)
	private MerchantStore merchantStore;

	@ManyToOne
	@JoinColumn(name = "PARENT_ID")
	private Category parent;

	@OneToMany(mappedBy = "parent", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Category> categories = new ArrayList<Category>();

	@Column(name = "CATEGORY_IMAGE", length = 100)
	private String categoryImage;

	@Column(name = "SORT_ORDER")
	private Integer sortOrder = 0;

	@Column(name = "CATEGORY_STATUS")
	private boolean categoryStatus;

	@Column(name = "VISIBLE")
	private boolean visible;

	@Column(name = "DEPTH")
	private Integer depth;

	@Column(name = "LINEAGE")
	private String lineage;

	@NotEmpty
	@Column(name = "CODE", length = 100, nullable = false)
	private String code;
	
	
	@Column(name="SEF_URL", length=120)
	private String seUrl;
	
	@Column(name = "CATEGORY_HIGHLIGHT")
	private String categoryHighlight;


	@Column(name="META_TITLE", length=120)
	private String metatagTitle;
	
	@Column(name="META_KEYWORDS")
	private String metatagKeywords;
	
	@Column(name="META_DESCRIPTION")
	private String metatagDescription;
	
	@NotEmpty
	@Column(name="NAME", nullable = false, length=120)
	private String name;
	
	@Column(name="TITLE", length=100)
	private String title;
	
	@Column(name="SUB_TITLE", length=100)
	private String subtitle;
	
	@Column(name="DESCRIPTION")
	@Lob
	private String description;
	

	public Category() {
	}

	public Category(MerchantStore store) {
		this.merchantStore = store;

	}


}