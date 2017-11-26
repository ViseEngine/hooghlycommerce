package co.hooghly.commerce.business;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.CustomerAttribute;
import co.hooghly.commerce.domain.CustomerOption;
import co.hooghly.commerce.domain.CustomerOptionSet;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.repository.CustomerOptionRepository;

@Service
public class CustomerOptionService extends SalesManagerEntityServiceImpl<Long, CustomerOption> {

	private CustomerOptionRepository customerOptionRepository;

	@Inject
	private CustomerAttributeService customerAttributeService;

	@Inject
	private CustomerOptionSetService customerOptionSetService;

	public CustomerOptionService(CustomerOptionRepository customerOptionRepository) {
		super(customerOptionRepository);
		this.customerOptionRepository = customerOptionRepository;
	}

	public List<CustomerOption> listByStore(MerchantStore store, Language language) throws ServiceException {

		return customerOptionRepository.findByStore(store.getId(), language.getId());

	}

	public void saveOrUpdate(CustomerOption entity) throws ServiceException {

		// save or update (persist and attach entities
		if (entity.getId() != null && entity.getId() > 0) {
			super.update(entity);
		} else {
			super.save(entity);
		}

	}

	public void delete(CustomerOption customerOption) throws ServiceException {

		// remove all attributes having this option
		List<CustomerAttribute> attributes = customerAttributeService.getByOptionId(customerOption.getMerchantStore(),
				customerOption.getId());

		for (CustomerAttribute attribute : attributes) {
			customerAttributeService.delete(attribute);
		}

		CustomerOption option = this.getById(customerOption.getId());

		List<CustomerOptionSet> optionSets = customerOptionSetService.listByOption(customerOption,
				customerOption.getMerchantStore());

		for (CustomerOptionSet optionSet : optionSets) {
			customerOptionSetService.delete(optionSet);
		}

		// remove option
		super.delete(option);

	}

	public CustomerOption getByCode(MerchantStore store, String optionCode) {
		return customerOptionRepository.findByCode(store.getId(), optionCode);
	}

}
