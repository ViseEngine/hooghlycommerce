package co.hooghly.commerce.web.ui;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class PersistableCustomerOptionValue extends CustomerOptionValueEntity
		implements Serializable {
	
	
	private List<CustomerOptionValueDescription> descriptions;

	

}
