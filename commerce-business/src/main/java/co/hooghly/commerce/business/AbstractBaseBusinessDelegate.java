package co.hooghly.commerce.business;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.transaction.annotation.Transactional;

import co.hooghly.commerce.domain.AbstractBaseEntity;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBaseBusinessDelegate<T extends AbstractBaseEntity, Id extends Serializable> {

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
	
	@Transactional(readOnly = true)
	public List<T> findAll() {
		return repository.findAll();
	}
	
	@Transactional
	public List<T> save(List<T> ts) {
		return repository.save(ts);
	}

	@Transactional
	public T save(T t) {
		return repository.save(t);

	}

	@Transactional
	public void delete(Id id) {
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