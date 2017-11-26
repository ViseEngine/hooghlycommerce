package co.hooghly.commerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.Language;

public interface LanguageRepository extends JpaRepository <Language, Integer> {
	
	Language findByCode(String code);
	


}
