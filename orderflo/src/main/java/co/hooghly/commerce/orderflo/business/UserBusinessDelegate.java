package co.hooghly.commerce.orderflo.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.orderflo.domain.User;
import co.hooghly.commerce.orderflo.roles.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class UserBusinessDelegate implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
//	@Transactional(transactionManager = "roleTransactionManager")
	public void register(User user)
	{
		log.info("User table is updated with default email ::", user);
		userRepository.save(user);
		
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails u=null;
		return u;
		
	}
}
