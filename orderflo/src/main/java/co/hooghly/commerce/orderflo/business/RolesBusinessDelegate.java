package co.hooghly.commerce.orderflo.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import co.hooghly.commerce.orderflo.domain.Role;
import co.hooghly.commerce.orderflo.roles.repository.RoleRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RolesBusinessDelegate extends AbstractBaseBusinessDelegate<Role, String> {

	@Autowired
	public RolesBusinessDelegate(RoleRepository repository) {
		super(repository);

	}
}
