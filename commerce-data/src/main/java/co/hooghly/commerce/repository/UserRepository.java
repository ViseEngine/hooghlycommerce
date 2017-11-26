package co.hooghly.commerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.hooghly.commerce.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query("select u from User as u inner join fetch u.groups ug inner join fetch u.merchantStore um left join fetch u.defaultLanguage ul where u.adminName = ?1")
	User findByUserName(String userName);

	@Query("select u from User as u inner join fetch u.groups ug inner join fetch u.merchantStore um left join fetch u.defaultLanguage ul where u.id = ?1")
	User findOne(Long id);
	
	@Query("select u from User as u inner join fetch u.groups ug inner join fetch u.merchantStore um left join fetch u.defaultLanguage ul order by u.id")
	List<User> findAll();
	
	@Query("select distinct u from User as u inner join fetch u.groups ug inner join fetch u.merchantStore um left join fetch u.defaultLanguage ul where um.id = ?1 order by u.id")
	List<User> findByStore(Integer storeId);
}
