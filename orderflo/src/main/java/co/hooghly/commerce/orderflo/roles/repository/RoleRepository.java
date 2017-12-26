package co.hooghly.commerce.orderflo.roles.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import co.hooghly.commerce.orderflo.domain.Role;

@Repository
public interface RoleRepository extends BaseRepository<Role, String>{
	Optional<Role> findByName(String role);
}
