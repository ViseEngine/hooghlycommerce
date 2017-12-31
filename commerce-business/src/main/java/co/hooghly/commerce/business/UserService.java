package co.hooghly.commerce.business;

import java.util.List;

import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.MerchantStore;
import co.hooghly.commerce.domain.User;
import co.hooghly.commerce.repository.UserRepository;

@Service
public class UserService extends SalesManagerEntityServiceImpl<Long, User> {

	private UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		super(userRepository);
		this.userRepository = userRepository;

	}

	// @Inject
	// private EmailService emailService;

	public User getByUserName(String userName) {

		return userRepository.findByUserName(userName);

	}

	public void delete(User user) {

		User u = this.getById(user.getId());
		super.delete(u);

	}

	public List<User> listUser() {

		return userRepository.findAll();

	}

	public List<User> listByStore(MerchantStore store) {

		return userRepository.findByStore(store.getId());

	}

	public void saveOrUpdate(User user) throws ServiceException {

		/*
		 * if(user.getId()==null || user.getId().longValue()==0) {
		 * userDao.save(user); } else { userDao.update(user); }
		 */

		userRepository.save(user);

	}

}
