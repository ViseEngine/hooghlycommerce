package co.hooghly.commerce.business;

import java.util.List;



import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.CustomerOption;
import co.hooghly.commerce.domain.CustomerOptionSet;
import co.hooghly.commerce.domain.CustomerOptionValue;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.repository.CustomerOptionSetRepository;

@Service
public class CustomerOptionSetService extends SalesManagerEntityServiceImpl<Long, CustomerOptionSet> {

	private CustomerOptionSetRepository customerOptionSetRepository;

	public CustomerOptionSetService(CustomerOptionSetRepository customerOptionSetRepository) {
		super(customerOptionSetRepository);
		this.customerOptionSetRepository = customerOptionSetRepository;
	}

	public List<CustomerOptionSet> listByOption(CustomerOption option, MerchantStore store) throws ServiceException {
		Validate.notNull(store, "merchant store cannot be null");
		Validate.notNull(option, "option cannot be null");

		return customerOptionSetRepository.findByOptionId(store.getId(), option.getId());
	}

	public void delete(CustomerOptionSet customerOptionSet) throws ServiceException {
		customerOptionSet = customerOptionSetRepository.findOne(customerOptionSet.getId());
		super.delete(customerOptionSet);
	}

	public List<CustomerOptionSet> listByStore(MerchantStore store, Language language) throws ServiceException {
		Validate.notNull(store, "merchant store cannot be null");

		return customerOptionSetRepository.findByStore(store.getId(), language.getId());
	}

	public void saveOrUpdate(CustomerOptionSet entity) throws ServiceException {
		Validate.notNull(entity, "customer option set cannot be null");

		if (entity.getId() > 0) {
			super.update(entity);
		} else {
			super.create(entity);
		}

	}

	public List<CustomerOptionSet> listByOptionValue(CustomerOptionValue optionValue, MerchantStore store)
			throws ServiceException {
		return customerOptionSetRepository.findByOptionValueId(store.getId(), optionValue.getId());
	}

}
