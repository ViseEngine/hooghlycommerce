package co.hooghly.commerce.business;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Category;
import co.hooghly.commerce.domain.Customer;
import co.hooghly.commerce.domain.Manufacturer;
import co.hooghly.commerce.domain.MerchantConfiguration;
import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.Order;
import co.hooghly.commerce.domain.TaxClass;
import co.hooghly.commerce.domain.User;
import co.hooghly.commerce.repository.MerchantStoreRepository;

@Service
public class MerchantStoreService extends SalesManagerEntityServiceImpl<Integer, MerchantStore> 
		 {
	

		
	@Autowired
	protected ProductTypeService productTypeService;
	
	@Autowired
	private TaxClassService taxClassService;
	
/*	@Autowired
	private ContentService contentService;
	
	@Autowired
	private MerchantConfigurationService merchantConfigurationService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CustomerService customerService;*/
	
	@Autowired
	private ManufacturerService manufacturerService;
	
	private MerchantStoreRepository merchantRepository;
	
	
	public MerchantStoreService(MerchantStoreRepository merchantRepository) {
		super(merchantRepository);
		this.merchantRepository = merchantRepository;
	}


	public MerchantStore getMerchantStore(String merchantStoreCode) throws ServiceException {
		return merchantRepository.findByCode(merchantStoreCode);
	}
	
	
	public void saveOrUpdate(MerchantStore store) throws ServiceException {
				
		super.save(store);

	}
	

	
	public MerchantStore getByCode(String code) throws ServiceException {
		
		return merchantRepository.findByCode(code);
	}
	
/*	
	public void delete(MerchantStore merchant) throws ServiceException {
		
		merchant = this.getById(merchant.getId());
		
		
		//reference
		List<Manufacturer> manufacturers = manufacturerService.listByStore(merchant);
		for(Manufacturer manufacturer : manufacturers) {
			manufacturerService.delete(manufacturer);
		}
		
		List<MerchantConfiguration> configurations = merchantConfigurationService.listByStore(merchant);
		for(MerchantConfiguration configuration : configurations) {
			merchantConfigurationService.delete(configuration);
		}
		

		//TODO taxService
		List<TaxClass> taxClasses = taxClassService.listByStore(merchant);
		for(TaxClass taxClass : taxClasses) {
			taxClassService.delete(taxClass);
		}
		
		//content
		contentService.removeFiles(merchant.getCode());
		//TODO staticContentService.removeImages
		
		//category / product
		List<Category> categories = categoryService.listByStore(merchant);
		for(Category category : categories) {
			categoryService.delete(category);
		}

		//users
		List<User> users = userService.listByStore(merchant);
		for(User user : users) {
			userService.delete(user);
		}
		
		//customers
		List<Customer> customers = customerService.listByStore(merchant);
		for(Customer customer : customers) {
			customerService.delete(customer);
		}
		
		//orders
		List<Order> orders = orderService.listByStore(merchant);
		for(Order order : orders) {
			orderService.delete(order);
		}
		
		super.delete(merchant);
		
	}*/

}
