package co.hooghly.commerce.orderflo.roles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.hooghly.commerce.orderflo.domain.Role;
import co.hooghly.commerce.orderflo.domain.User;


@Repository
public interface UserRepository extends BaseRepository<User, String>{
	User findByEmail(String email);
	User findFirstByApiToken(String apiToken);
}
