package co.hooghly.commerce.orderflo.repository;

import org.springframework.stereotype.Repository;

import co.hooghly.commerce.orderflo.domain.User;


@Repository
public interface UserRepository extends BaseRepository<User, String>{
	User findByEmail(String email);
	User findFirstByApiToken(String apiToken);
}
