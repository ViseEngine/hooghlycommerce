package co.hooghly.commerce.business;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.CustomerAttribute;
import co.hooghly.commerce.domain.CustomerOptionSet;
import co.hooghly.commerce.domain.CustomerOptionValue;
import co.hooghly.commerce.domain.Language;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.repository.CustomerOptionValueRepository;

@Service
public class CustomerOptionValueService extends SalesManagerEntityServiceImpl<Long, CustomerOptionValue> {

	@Autowired
	private CustomerAttributeService customerAttributeService;

	private CustomerOptionValueRepository customerOptionValueRepository;

	@Autowired
	private CustomerOptionSetService customerOptionSetService;

	public CustomerOptionValueService(CustomerOptionValueRepository customerOptionValueRepository) {
		super(customerOptionValueRepository);
		this.customerOptionValueRepository = customerOptionValueRepository;
	}

	public List<CustomerOptionValue> listByStore(MerchantStore store, Language language)  {

		return customerOptionValueRepository.findByStore(store.getId(), language.getId());
	}

	

	public void delete(CustomerOptionValue customerOptionValue) {

		// remove all attributes having this option
		List<CustomerAttribute> attributes = customerAttributeService
				.getByCustomerOptionValueId(customerOptionValue.getMerchantStore(), customerOptionValue.getId());

		for (CustomerAttribute attribute : attributes) {
			customerAttributeService.delete(attribute);
		}

		List<CustomerOptionSet> optionSets = customerOptionSetService.listByOptionValue(customerOptionValue,
				customerOptionValue.getMerchantStore());

		for (CustomerOptionSet optionSet : optionSets) {
			customerOptionSetService.delete(optionSet);
		}

		CustomerOptionValue option = super.getById(customerOptionValue.getId());

		// remove option
		super.delete(option);

	}

	public CustomerOptionValue getByCode(MerchantStore store, String optionValueCode) {
		return customerOptionValueRepository.findByCode(store.getId(), optionValueCode);
	}

}
