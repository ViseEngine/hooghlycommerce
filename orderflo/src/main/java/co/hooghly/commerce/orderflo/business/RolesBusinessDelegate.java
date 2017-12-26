package co.hooghly.commerce.orderflo.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.orderflo.domain.Role;
import co.hooghly.commerce.orderflo.repository.RoleRepository;


@Component
public class RolesBusinessDelegate extends AbstractBaseBusinessDelegate<Role, Long> {

	@Autowired
	public RolesBusinessDelegate(RoleRepository repository) {
		super(repository);

	}
}
