package co.hooghly.commerce.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table (name="ORDER_ACCOUNT_PRODUCT" )
public class OrderAccountProduct implements Serializable {
	private static final long serialVersionUID = -7437197293537758668L;

	@Id
	@Column (name="ORDER_ACCOUNT_PRODUCT_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderAccountProductId;

	@ManyToOne
	@JoinColumn(name = "ORDER_ACCOUNT_ID" , nullable=false)
	private OrderAccount orderAccount;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORDER_PRODUCT_ID" , nullable=false)
	private OrderProduct orderProduct;

	@Temporal(TemporalType.DATE)
	@Column (name="ORDER_ACCOUNT_PRODUCT_ST_DT" , length=0 , nullable=false)
	private Date orderAccountProductStartDate;

	@Temporal(TemporalType.DATE)
	@Column (name="ORDER_ACCOUNT_PRODUCT_END_DT", length=0)
	private Date orderAccountProductEndDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column (name="ORDER_ACCOUNT_PRODUCT_EOT"  , length=0 )
	private Date orderAccountProductEot;

	@Temporal(TemporalType.DATE)
	@Column (name="ORDER_ACCOUNT_PRODUCT_ACCNT_DT"  , length=0 )
	private Date orderAccountProductAccountedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column (name="ORDER_ACCOUNT_PRODUCT_L_ST_DT"  , length=0 )
	private Date orderAccountProductLastStatusDate;

	@Column (name="ORDER_ACCOUNT_PRODUCT_L_TRX_ST" , nullable=false )
	private Integer orderAccountProductLastTransactionStatus;

	@Column (name="ORDER_ACCOUNT_PRODUCT_PM_FR_TY" , nullable=false )
	private Integer orderAccountProductPaymentFrequencyType;

	@Column (name="ORDER_ACCOUNT_PRODUCT_STATUS" , nullable=false )
	private Integer orderAccountProductStatus;

	public OrderAccountProduct() {
	}

	public Long getOrderAccountProductId() {
		return orderAccountProductId;
	}

	public void setOrderAccountProductId(Long orderAccountProductId) {
		this.orderAccountProductId = orderAccountProductId;
	}

	public OrderAccount getOrderAccount() {
		return orderAccount;
	}

	public void setOrderAccount(OrderAccount orderAccount) {
		this.orderAccount = orderAccount;
	}

	public OrderProduct getOrderProduct() {
		return orderProduct;
	}

	public void setOrderProduct(OrderProduct orderProduct) {
		this.orderProduct = orderProduct;
	}

	public Date getOrderAccountProductStartDate() {
		return orderAccountProductStartDate;
	}

	public void setOrderAccountProductStartDate(Date orderAccountProductStartDate) {
		this.orderAccountProductStartDate = orderAccountProductStartDate;
	}

	public Date getOrderAccountProductEndDate() {
		return orderAccountProductEndDate;
	}

	public void setOrderAccountProductEndDate(Date orderAccountProductEndDate) {
		this.orderAccountProductEndDate = orderAccountProductEndDate;
	}

	public Date getOrderAccountProductEot() {
		return orderAccountProductEot;
	}

	public void setOrderAccountProductEot(Date orderAccountProductEot) {
		this.orderAccountProductEot = orderAccountProductEot;
	}

	public Date getOrderAccountProductAccountedDate() {
		return orderAccountProductAccountedDate;
	}

	public void setOrderAccountProductAccountedDate(
			Date orderAccountProductAccountedDate) {
		this.orderAccountProductAccountedDate = orderAccountProductAccountedDate;
	}

	public Date getOrderAccountProductLastStatusDate() {
		return orderAccountProductLastStatusDate;
	}

	public void setOrderAccountProductLastStatusDate(
			Date orderAccountProductLastStatusDate) {
		this.orderAccountProductLastStatusDate = orderAccountProductLastStatusDate;
	}

	public Integer getOrderAccountProductLastTransactionStatus() {
		return orderAccountProductLastTransactionStatus;
	}

	public void setOrderAccountProductLastTransactionStatus(
			Integer orderAccountProductLastTransactionStatus) {
		this.orderAccountProductLastTransactionStatus = orderAccountProductLastTransactionStatus;
	}

	public Integer getOrderAccountProductPaymentFrequencyType() {
		return orderAccountProductPaymentFrequencyType;
	}

	public void setOrderAccountProductPaymentFrequencyType(
			Integer orderAccountProductPaymentFrequencyType) {
		this.orderAccountProductPaymentFrequencyType = orderAccountProductPaymentFrequencyType;
	}

	public Integer getOrderAccountProductStatus() {
		return orderAccountProductStatus;
	}

	public void setOrderAccountProductStatus(Integer orderAccountProductStatus) {
		this.orderAccountProductStatus = orderAccountProductStatus;
	}
}
