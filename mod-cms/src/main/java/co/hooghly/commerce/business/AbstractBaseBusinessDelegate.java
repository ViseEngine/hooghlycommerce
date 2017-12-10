package co.hooghly.commerce.business;

import java.io.Serializable;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.transaction.annotation.Transactional;

import co.hooghly.commerce.domain.BaseEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBaseBusinessDelegate<T extends BaseEntity, Id extends Serializable> {
	@Getter
	private JpaRepository<T, Id> repository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Transactional
	public void publishEvent(ApplicationEvent event) {
		log.debug("### Publish Events ####");
		eventPublisher.publishEvent(event);
	}

	public AbstractBaseBusinessDelegate(JpaRepository<T, Id> repository) {
		super();
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public T findOne(Id id) {
		return repository.findOne(id);
	}

	

	@Transactional
	public T save(T t) {
		log.debug("saving ---> " + t);
		return repository.save(t);
	}

	@Transactional
	public void delete(Id id) {

		
	}

	@Transactional
	public void purge(Id id) {
		repository.delete(id);
	}

	
	@Transactional
	public void deleteAll(Id[] ids, String parentId) {
		// Since we are getting unique id of the entity we do not require to use
		// the parent id here.
		for (Id id : ids) {
			delete(id);
		}

	}

	
	

	
}