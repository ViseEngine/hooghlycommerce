package co.hooghly.commerce.orderflo.business;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Component;

import co.hooghly.commerce.orderflo.domain.User;
import co.hooghly.commerce.orderflo.repository.UserRepository;



@Component
public class UserBusinessDelegate extends AbstractBaseBusinessDelegate<User, String> implements UserDetailsService {

	
	public UserBusinessDelegate(UserRepository repository) {
		super(repository);

	}

	public UserDetails loadUserByUsername(String username)  {
		UserDetails u = null;
		return u;

	}
}
