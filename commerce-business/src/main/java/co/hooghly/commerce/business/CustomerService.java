package co.hooghly.commerce.business;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.NotImplementedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Address;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.CustomerAttribute;
import co.hooghly.commerce.domain.CustomerCriteria;
import co.hooghly.commerce.domain.CustomerList;
import co.hooghly.commerce.domain.MerchantStore;
//import co.hooghly.commerce.modules.GeoLocation;
import co.hooghly.commerce.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CustomerService extends SalesManagerEntityServiceImpl<Long, Customer> {

	private CustomerRepository customerRepository;

	@Autowired
	private CustomerAttributeService customerAttributeService;

	//@Autowired
	//private GeoLocation geoLocation;

	
	public CustomerService(CustomerRepository customerRepository) {
		super(customerRepository);
		this.customerRepository = customerRepository;
	}
	
	public Customer findByMerchantStoreIdAndId(int merchantStoreId, Long id) {
		return customerRepository.findByMerchantStoreIdAndId(merchantStoreId, id);
	}
	
	public List<Customer> getByName(String firstName) {
		return customerRepository.findByName(firstName);
	}

	public Customer getById(Long id) {
		return customerRepository.findOne(id);
	}

	public Customer getByNick(String nick) {
		return customerRepository.findByNick(nick);
	}

	public Customer getByNick(String nick, int storeId) {
		return customerRepository.findByNick(nick, storeId);
	}

	public List<Customer> listByStore(MerchantStore store) {
		return customerRepository.findByStore(store.getId());
	}

	public CustomerList listByStore(MerchantStore store, CustomerCriteria criteria) {
		return customerRepository.listByStore(store, criteria);
	}

	public Optional<Address> getCustomerAddress(MerchantStore store, String ipAddress) {

		//return geoLocation.getAddress(ipAddress);
		throw new NotImplementedException("Not yet implemented");

	}

	public void saveOrUpdate(Customer customer) throws ServiceException {

		log.debug("Creating Customer");
		super.create(customer);
	}

	public void delete(Customer customer)  {
		customer = getById(customer.getId());

		// delete attributes
		List<CustomerAttribute> attributes = customerAttributeService.getByCustomer(customer.getMerchantStore(),
				customer);
		if (attributes != null) {
			for (CustomerAttribute attribute : attributes) {
				customerAttributeService.delete(attribute);
			}
		}
		customerRepository.delete(customer);

	}
	
	public void deleteByMerchantStoreIdAndId(int merchantStoreId, Long id) {
		customerRepository.deleteByMerchantStoreIdAndId(merchantStoreId, id);
	}

}
