package co.hooghly.commerce.domain;


import java.util.Map;

import javax.persistence.*;


import lombok.Data;

@Data
@Entity 
@Table(name = "PAGE_DEFINITION")
public class PageDefinition {
	
	@Id
	@Column(name="PAGE_DEFINITION_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="LAYOUT")
	private String layout = "layouts/default";
	
	@Column(name = "TITLE")
	private String title;
	
	@ElementCollection
    @MapKeyColumn(name="FRAGMENT_NAME")
    @Column(name="FRAGMENT_VALUE")
    @CollectionTable(name="PAGE_fRAGMENTS", joinColumns=@JoinColumn(name="PAGE_DEFINITION_ID"))
	private Map<String,String> fragments;
	
	@Column(name = "PAGE_TITLE")
	private String pageTitle;
	
	@Column(name = "PAGE_DESCRIPTION")
	private String description;
	
	@Column(name = "PAGE_KEYWORDS")
	private String keywords;
	
	@Column(name = "PAGE_URL")
	private String pageUrl;
	
}