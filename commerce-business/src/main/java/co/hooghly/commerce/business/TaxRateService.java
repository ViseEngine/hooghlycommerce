package co.hooghly.commerce.business;

import java.util.List;


import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Country;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.TaxClass;
import co.hooghly.commerce.domain.TaxRate;
import co.hooghly.commerce.domain.Zone;
import co.hooghly.commerce.repository.TaxRateRepository;

@Service
public class TaxRateService extends SalesManagerEntityServiceImpl<Long, TaxRate>{

	private TaxRateRepository taxRateRepository;
	
	
	public TaxRateService(TaxRateRepository taxRateRepository) {
		super(taxRateRepository);
		this.taxRateRepository = taxRateRepository;
	}

	
	public List<TaxRate> listByStore(MerchantStore store)
			throws ServiceException {
		return taxRateRepository.findByStore(store.getId());
	}
	
	
	public List<TaxRate> listByStore(MerchantStore store, Language language)
			throws ServiceException {
		return taxRateRepository.findByStoreAndLanguage(store.getId(), language.getId());
	}
	
	
	
	public TaxRate getByCode(String code, MerchantStore store)
			throws ServiceException {
		return taxRateRepository.findByStoreAndCode(store.getId(), code);
	}
	
	
	public List<TaxRate> listByCountryZoneAndTaxClass(Country country, Zone zone, TaxClass taxClass, MerchantStore store, Language language) throws ServiceException {
		//return taxRateDao.listByCountryZoneAndTaxClass(country, zone, taxClass, store, language);
		return taxRateRepository.findByMerchantAndZoneAndCountryAndLanguage(store.getId(), zone.getId(), country.getId(), language.getId());
	}
	
	
	public List<TaxRate> listByCountryStateProvinceAndTaxClass(Country country, String stateProvince, TaxClass taxClass, MerchantStore store, Language language) throws ServiceException {
		//return taxRateDao.listByCountryStateProvinceAndTaxClass(country, stateProvince, taxClass, store, language);
		return taxRateRepository.findByMerchantAndProvinceAndCountryAndLanguage(store.getId(), stateProvince, country.getId(), language.getId());
	}
	
	
	public void delete(TaxRate taxRate) throws ServiceException {
		
		//TaxRate t = this.getById(taxRate.getId());
		//super.delete(t);
		taxRateRepository.delete(taxRate);
		
	}
		

	
}
