package co.hooghly.commerce.business;

import java.util.List;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Manufacturer;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.repository.ManufacturerRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ManufacturerService extends AbstractBaseBusinessDelegate<Manufacturer, Long> {

	private ManufacturerRepository manufacturerRepository;

	public ManufacturerService(ManufacturerRepository manufacturerRepository) {
		super(manufacturerRepository);
		this.manufacturerRepository = manufacturerRepository;
	}

	public void delete(Manufacturer manufacturer) {
		manufacturer = findOne(manufacturer.getId());
		delete(manufacturer);
	}

	public Long getCountManufAttachedProducts(Manufacturer manufacturer) {
		return manufacturerRepository.countByProduct(manufacturer.getId());

	}

	public List<Manufacturer> listByStore(MerchantStore store, Language language) {
		return manufacturerRepository.findByMerchantStoreId(store.getId());
	}

	public List<Manufacturer> listByStore(MerchantStore store) {
		return manufacturerRepository.findByMerchantStoreId(store.getId());
	}

	public List<Manufacturer> listByProductsByCategoriesId(MerchantStore store, List<Long> ids, Language language) {
		return manufacturerRepository.findByCategories(ids);
	}

	public Manufacturer getByCode(co.hooghly.commerce.domain.MerchantStore store, String code) {
		return manufacturerRepository.findByCodeAndMerchantStoreId(code, store.getId());
	}
}
