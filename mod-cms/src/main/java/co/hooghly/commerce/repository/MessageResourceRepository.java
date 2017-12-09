package co.hooghly.commerce.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.hooghly.commerce.domain.MessageResource;

@Repository
public interface MessageResourceRepository extends JpaRepository<MessageResource, String>{
	MessageResource findByMessageKeyAndLocale(String messageKey, String locale);
}