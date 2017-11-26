package co.hooghly.commerce.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


@Entity
@Table(name="CUSTOMER_OPTION_DESC", uniqueConstraints={
	@UniqueConstraint(columnNames={
			"CUSTOMER_OPTION_ID",
			"LANGUAGE_ID"
		})
	}
)
public class CustomerOptionDescription extends Description {
	private static final long serialVersionUID = -3158504904707188465L;
	
	@ManyToOne(targetEntity = CustomerOption.class)
	@JoinColumn(name = "CUSTOMER_OPTION_ID", nullable = false)
	private CustomerOption customerOption;
	
	@Column(name="CUSTOMER_OPTION_COMMENT")
	@Lob
	private String customerOptionComment;
	

	
	public CustomerOptionDescription() {
	}

	public CustomerOption getCustomerOption() {
		return customerOption;
	}

	public void setCustomerOption(CustomerOption customerOption) {
		this.customerOption = customerOption;
	}

	public String getCustomerOptionComment() {
		return customerOptionComment;
	}

	public void setCustomerOptionComment(String customerOptionComment) {
		this.customerOptionComment = customerOptionComment;
	}


	

}
