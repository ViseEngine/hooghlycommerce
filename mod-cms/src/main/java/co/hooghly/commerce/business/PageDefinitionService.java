package co.hooghly.commerce.business;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.hooghly.commerce.domain.PageDefinition;
import co.hooghly.commerce.repository.PageDefinitionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PageDefinitionService {
	@Autowired
	private PageDefinitionRepository pageDefinitionRepository;
	
	@Transactional(readOnly = true)
	@Cacheable("page-def-by-name")
	public Optional<PageDefinition> findByName(String name){
		return pageDefinitionRepository.findByName(name);
	}
}
