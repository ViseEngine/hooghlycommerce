package co.hooghly.commerce.orderflo.business;

import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Component;

import co.hooghly.commerce.orderflo.domain.SimpleUserDetails;
import co.hooghly.commerce.orderflo.domain.User;
import co.hooghly.commerce.orderflo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class UserBusinessDelegate extends AbstractBaseBusinessDelegate<User, String> implements UserDetailsService {
	
	

	
	private UserRepository repository;

	public UserBusinessDelegate(UserRepository repository) {
		super(repository);
		this.repository = repository;
	}

	public UserDetails loadUserByUsername(String username) {
		
		System.out.println("username :"+username);
		Optional<User> user = repository.findByEmail(username);
		SimpleUserDetails userDetails = null;
		if(user.isPresent()) {
			log.info("User found.");
			userDetails = new SimpleUserDetails(user.get());
		}
		System.out.println("userDetails :"+userDetails);
		return userDetails;
	}

	
}
