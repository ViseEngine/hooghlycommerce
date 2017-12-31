package co.hooghly.commerce.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "SLIDE_SHOW_ITEM",uniqueConstraints=
    @UniqueConstraint(columnNames = {"MERCHANT_ID", "CODE"}) )
@Data
@EqualsAndHashCode(callSuper=false)
public class SlideShowItem extends SalesManagerEntity<Long, SlideShowItem> implements Serializable {



	private static final long serialVersionUID = 1772757159185494620L;
	
	@Id
	@Column(name = "CMS_CONTENT_ID", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Embedded
	private AuditSection auditSection = new AuditSection();
	
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MERCHANT_ID", nullable=false)
	private MerchantStore merchantStore;
	
	@NotEmpty
	@Column(name="CODE", length=100, nullable=false)
	private String code;
	
	@Column(name = "DELETED")
	private boolean deleted;
	
	@Column(name = "ENABLED")
	private boolean enabled;
	
	@Column(name="SEF_URL", length=120)
	private String seUrl;

	@Column(name="MESSAGE_KEY")
	private String messageKey;
	
	@Column(name="META_KEYWORDS")
	private String metatagKeywords;
	
	@Column(name="META_TITLE")
	private String metatagTitle;
	
	@Column(name="META_DESCRIPTION")
	private String metatagDescription;
	
	
	
	
	@Column(name = "SORT_ORDER")
	private Integer sortOrder = 0;
	
	@Lob
	private String templateContent;
	
	@Lob
    @Column(name="STATIC_CONTENT_DATA", nullable=true, columnDefinition="mediumblob")
    private byte[] content;
	
	@Column(name = "FILE_SIZE")
	private Long fileSize;
	
	@Column(name = "ORIGINAL_FILE_NAME")
	private String originalFileName;
	
	@Column(name="FILE_CONTENT_TYPE")
	private String contentType;
	
	@Column(name="LAST_MODIFIED_TIME")
	private Long lastModified;
	
	@Column(name="THEME")
	private String theme;
	
	@Column(name="FOLDER")
	private String folder;
	
	@Column(name="FILE_TYPE")
	private String fileType;
	
}