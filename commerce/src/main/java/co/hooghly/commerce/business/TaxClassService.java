package co.hooghly.commerce.business;

import java.util.List;


import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.TaxClass;
import co.hooghly.commerce.repository.TaxClassRepository;

@Service
public class TaxClassService extends SalesManagerEntityServiceImpl<Long, TaxClass> {

	private TaxClassRepository taxClassRepository;
	
	public TaxClassService(TaxClassRepository taxClassRepository) {
		super(taxClassRepository);
		
		this.taxClassRepository = taxClassRepository;
	}
	
	
	public List<TaxClass> listByStore(MerchantStore store) throws ServiceException {	
		return taxClassRepository.findByStore(store.getId());
	}
	
	
	public TaxClass getByCode(String code) throws ServiceException {
		return taxClassRepository.findByCode(code);
	}
	
	
	public TaxClass getByCode(String code, MerchantStore store) throws ServiceException {
		return taxClassRepository.findByStoreAndCode(store.getId(), code);
	}
	
	
	public void delete(TaxClass taxClass) throws ServiceException {
		
		TaxClass t = this.getById(taxClass.getId());
		super.delete(t);
		
	}
	
	
	public TaxClass getById(Long id) {
		return taxClassRepository.findOne(id);
	}

	
	public TaxClass saveNow(TaxClass taxClass) {
		return taxClassRepository.save(taxClass);
	}
	

}
