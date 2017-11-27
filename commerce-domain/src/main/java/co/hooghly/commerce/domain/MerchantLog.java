package co.hooghly.commerce.domain;

import java.io.Serializable;

import javax.persistence.Column;
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


@Entity
@EntityListeners(value = AuditListener.class)
@Table(name = "MERCHANT_LOG")
public class MerchantLog extends SalesManagerEntity<Long, MerchantLog> implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "MERCHANT_LOG_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MERCHANT_ID", nullable=false)
	private MerchantStore store;

	@Column(name="MODULE", length=25, nullable=true)
	private String module;
	

	@Column(name="LOG")
	@Lob
	private String log;
	
	public MerchantLog(MerchantStore store, String log) {
		this.store = store;
		this.log = log;
	}
	
	public MerchantLog(MerchantStore store, String module, String log) {
		this.store = store;
		this.module = module;
		this.log = log;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public MerchantStore getStore() {
		return store;
	}


	public void setStore(MerchantStore store) {
		this.store = store;
	}


	public String getModule() {
		return module;
	}


	public void setModule(String module) {
		this.module = module;
	}


	public String getLog() {
		return log;
	}


	public void setLog(String log) {
		this.log = log;
	}


}
