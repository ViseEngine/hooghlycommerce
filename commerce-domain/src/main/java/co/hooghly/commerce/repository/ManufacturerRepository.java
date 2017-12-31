package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.Manufacturer;

public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {

	@Query("select count(distinct p) from Product as p where p.manufacturer.id=?1")
	Long countByProduct(Long manufacturerId);
	
	
	
	List<Manufacturer> findByMerchantStoreId(Integer storeId);
	
	@Query("select distinct manufacturer from Product as p join p.manufacturer manufacturer join p.categories categs where categs.id in (?1) ")
	List<Manufacturer> findByCategories(List<Long> categoryIds);
	
	Manufacturer findByCodeAndMerchantStoreId(String code, Integer storeId);
}
