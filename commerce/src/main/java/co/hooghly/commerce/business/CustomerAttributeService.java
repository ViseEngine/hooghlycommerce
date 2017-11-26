package co.hooghly.commerce.business;

import java.util.List;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.CustomerAttribute;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.repository.CustomerAttributeRepository;

@Service
public class CustomerAttributeService extends SalesManagerEntityServiceImpl<Long, CustomerAttribute> {

	private CustomerAttributeRepository customerAttributeRepository;

	public CustomerAttributeService(CustomerAttributeRepository customerAttributeRepository) {
		super(customerAttributeRepository);
		this.customerAttributeRepository = customerAttributeRepository;
	}

	public void saveOrUpdate(CustomerAttribute customerAttribute) throws ServiceException {

		customerAttributeRepository.save(customerAttribute);

	}

	public void delete(CustomerAttribute attribute) throws ServiceException {

		// override method, this allows the error that we try to remove a
		// detached instance
		attribute = this.getById(attribute.getId());
		super.delete(attribute);

	}

	public CustomerAttribute getByCustomerOptionId(MerchantStore store, Long customerId, Long id) {
		return customerAttributeRepository.findByOptionId(store.getId(), customerId, id);
	}

	public List<CustomerAttribute> getByCustomer(MerchantStore store, Customer customer) {
		return customerAttributeRepository.findByCustomerId(store.getId(), customer.getId());
	}

	public List<CustomerAttribute> getByCustomerOptionValueId(MerchantStore store, Long id) {
		return customerAttributeRepository.findByOptionValueId(store.getId(), id);
	}

	public List<CustomerAttribute> getByOptionId(MerchantStore store, Long id) {
		return customerAttributeRepository.findByOptionId(store.getId(), id);
	}

}
