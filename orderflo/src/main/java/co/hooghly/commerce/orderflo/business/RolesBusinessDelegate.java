package co.hooghly.commerce.orderflo.business;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import co.hooghly.commerce.orderflo.domain.Role;
import co.hooghly.commerce.orderflo.repository.RoleRepository;


@Component
public class RolesBusinessDelegate extends AbstractBaseBusinessDelegate<Role, Long> {
	
	private RoleRepository repository;
	
	public RolesBusinessDelegate(RoleRepository repository) {
		super(repository);
		this.repository = repository;
	}
	
	@Transactional(readOnly=true)
	public Optional<Role> findByName(String roleName) {
		return repository.findByName(roleName);
	}
}
