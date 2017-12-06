package co.hooghly.commerce.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.hooghly.commerce.domain.PageDefinition;

@Repository
public interface PageDefinitionRepository extends JpaRepository<PageDefinition, Long>{
	
	Optional<PageDefinition> findByName(String name);
	
}
