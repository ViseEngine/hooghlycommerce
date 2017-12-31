package co.hooghly.commerce.business;



import org.springframework.stereotype.Service;


import co.hooghly.commerce.domain.ProductType;
import co.hooghly.commerce.repository.ProductTypeRepository;

@Service
public class ProductTypeService extends AbstractBaseBusinessDelegate<ProductType, Long>{

	private ProductTypeRepository productTypeRepository;

	public ProductTypeService(ProductTypeRepository productTypeRepository) {
		super(productTypeRepository);
		this.productTypeRepository = productTypeRepository;
	}

	public ProductType getProductType(String productTypeCode) {

		return productTypeRepository.findByCode(productTypeCode);

	}

}
