package co.hooghly.commerce.web.ui;

import java.io.Serializable;
import java.util.List;


@Deprecated
public class PersistableCustomer extends CustomerEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<PersistableCustomerAttribute> attributes;
	public void setAttributes(List<PersistableCustomerAttribute> attributes) {
		this.attributes = attributes;
	}
	public List<PersistableCustomerAttribute> getAttributes() {
		return attributes;
	}
	

}
