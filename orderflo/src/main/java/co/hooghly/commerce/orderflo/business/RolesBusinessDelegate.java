package co.hooghly.commerce.orderflo.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.hooghly.commerce.orderflo.domain.Role;
import co.hooghly.commerce.orderflo.roles.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class RolesBusinessDelegate implements UserDetailsService{

	@Autowired
	private RoleRepository roleRepository;
	
	@Transactional(transactionManager = "roleTransactionManager")
	public void register(Role role)
	{
		roleRepository.save(role);
		
	}
	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails u=null;
		return u;
		
	}
}
