package co.hooghly.commerce.business;

import java.util.HashSet;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.Manufacturer;
import co.hooghly.commerce.domain.ManufacturerDescription;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.repository.ManufacturerRepository;



@Service
public class ManufacturerService extends
		SalesManagerEntityServiceImpl<Long, Manufacturer>  {

	private static final Logger LOGGER = LoggerFactory.getLogger(ManufacturerService.class);

	private ManufacturerRepository manufacturerRepository;
	
	
	public ManufacturerService(
		ManufacturerRepository manufacturerRepository) {
		super(manufacturerRepository);
		this.manufacturerRepository = manufacturerRepository;		
	}
	
	
	public void delete(Manufacturer manufacturer) throws ServiceException{
		manufacturer =  this.getById(manufacturer.getId() );
		super.delete( manufacturer );
	}
	
	
	public Long getCountManufAttachedProducts( Manufacturer manufacturer ) throws ServiceException {
		return manufacturerRepository.countByProduct(manufacturer.getId());
				//.getCountManufAttachedProducts( manufacturer );
	}
	
	
	
	public List<Manufacturer> listByStore(MerchantStore store, Language language) throws ServiceException {
		return manufacturerRepository.findByStoreAndLanguage(store.getId(), language.getId());
	}
	
	
	public List<Manufacturer> listByStore(MerchantStore store) throws ServiceException {
		return manufacturerRepository.findByStore(store.getId());
	}
	
	
	public List<Manufacturer> listByProductsByCategoriesId(MerchantStore store, List<Long> ids, Language language) throws ServiceException {
		return manufacturerRepository.findByCategoriesAndLanguage(ids, language.getId());
	}

	
	public void addManufacturerDescription(Manufacturer manufacturer, ManufacturerDescription description)
			throws ServiceException {
		
		
		if(manufacturer.getDescriptions()==null) {
			manufacturer.setDescriptions(new HashSet<ManufacturerDescription>());
		}
		
		manufacturer.getDescriptions().add(description);
		description.setManufacturer(manufacturer);
		update(manufacturer);
	}
	
		
	public void saveOrUpdate(Manufacturer manufacturer) throws ServiceException {

		LOGGER.debug("Creating Manufacturer");
		
		if(manufacturer.getId()!=null && manufacturer.getId()>0) {
		   super.update(manufacturer);  
			
		} else {						
		   super.create(manufacturer);

		}
	}

	
	public Manufacturer getByCode(co.hooghly.commerce.domain.MerchantStore store, String code) {
		return manufacturerRepository.findByCodeAndMerchandStore(code, store.getId());
	}
}
