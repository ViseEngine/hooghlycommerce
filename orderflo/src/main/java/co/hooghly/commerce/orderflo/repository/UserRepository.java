package co.hooghly.commerce.orderflo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import co.hooghly.commerce.orderflo.domain.User;


@Repository
public interface UserRepository extends JpaRepository<User, String>{
	Optional<User> findByEmail(String email);
	User findFirstByApiToken(String apiToken);
}
