package co.hooghly.commerce.startup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.business.ProductTypeService;

import co.hooghly.commerce.domain.ProductType;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(5)
public class SubreferencePopulator extends AbstractDataPopulator {
	
	public SubreferencePopulator() {
		super("SUBREFERENCE");
	}

	@Autowired
	protected ProductTypeService productTypeService;

	@Override
	public void runInternal(String... args) throws Exception {
		createSubReferences();

	}

	private void createSubReferences() {

		log.info("5.Loading Sub reference/product type.");

		ProductType productType = new ProductType();
		productType.setCode(ProductType.GENERAL_TYPE);
		productTypeService.create(productType);

	}
}
