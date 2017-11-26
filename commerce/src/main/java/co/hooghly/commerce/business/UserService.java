package co.hooghly.commerce.business;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.User;
import co.hooghly.commerce.repository.UserRepository;



@Service
public class UserService extends SalesManagerEntityServiceImpl<Long, User>
		 {


	private UserRepository userRepository;
	
	@Inject
	public UserService(UserRepository userRepository) {
		super(userRepository);
		this.userRepository = userRepository;

	}
	
	@Inject
	private EmailService emailService;
	
	
	public User getByUserName(String userName) throws ServiceException {
		
		return userRepository.findByUserName(userName);
		
	}
	
	
	public void delete(User user) throws ServiceException {
		
		User u = this.getById(user.getId());
		super.delete(u);
		
	}

	
	public List<User> listUser() throws ServiceException {
		try {
			return userRepository.findAll();
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}
	
	
	public List<User> listByStore(MerchantStore store) throws ServiceException {
		try {
			return userRepository.findByStore(store.getId());
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	
	
	public void saveOrUpdate(User user) throws ServiceException {
		
/*		if(user.getId()==null || user.getId().longValue()==0) {
			userDao.save(user);
		} else {
			userDao.update(user);
		}*/
		
		userRepository.save(user);
		
	}

}
