package co.hooghly.commerce.orderflo.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.stereotype.Component;


import co.hooghly.commerce.orderflo.domain.Role;
import co.hooghly.commerce.orderflo.domain.User;
import co.hooghly.commerce.orderflo.repository.UserRepository;
import co.hooghly.commerce.orderflo.simpleusers.SimpleUsers;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class UserBusinessDelegate extends AbstractBaseBusinessDelegate<User, String> implements UserDetailsService {

	@Autowired
	private UserRepository repository;
	
	public UserBusinessDelegate(UserRepository repository) {
		super(repository);

	}

	public UserDetails loadUserByUsername(String username)  {
		UserDetails u = null;
		User user=repository.findByEmail(username);
		System.out.println("Username :"+username);
		System.out.println(user.getEmail() +"::"+user.getPassword() +"::"+user.getRole() +"::"+user.getRoles());
	//	return (UserDetails)user;
		return new SimpleUsers(user,getAuthorities(user.getRoles()));

	}
	
	private List<GrantedAuthority> getAuthorities(List<Role> roles) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (Role role : roles) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
			System.out.println("ROLE_" + role.getName());
		}

		log.info("Authorities - # {} - ", authorities);

		return authorities;
	}
}
