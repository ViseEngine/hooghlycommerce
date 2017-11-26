package co.hooghly.commerce.web.ui;

import java.io.Serializable;
import java.util.List;

public class PersistableProductOption extends ProductOptionEntity implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<ProductOptionDescription> descriptions;
	public void setDescriptions(List<ProductOptionDescription> descriptions) {
		this.descriptions = descriptions;
	}
	public List<ProductOptionDescription> getDescriptions() {
		return descriptions;
	}

}
