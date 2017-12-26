package co.hooghly.commerce.orderflo.business;

import java.io.Serializable;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

import co.hooghly.commerce.orderflo.roles.repository.BaseRepository;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractBaseBusinessDelegate<T , Id extends Serializable> {
	@Getter
	private BaseRepository<T, Id> repository;

	@Autowired
	private ApplicationEventPublisher eventPublisher;

	@Transactional
	public void publishEvent(ApplicationEvent event) {
		log.debug("### Publish Events ####");
		eventPublisher.publishEvent(event);
	}

	public AbstractBaseBusinessDelegate(BaseRepository<T, Id> repository) {
		super();
		this.repository = repository;
	}

	@Transactional(readOnly = true)
	public T findOne(Id id) {
		return repository.findOne(id);
	}


	
	/*@Transactional(readOnly = true)
	public List<T> findAllByDeleted(boolean deleted) {
		return repository.findAllByDeleted(deleted);
	}*/

	@Transactional
	public T save(T t) {
		log.debug("saving ---> " + t);
		return repository.save(t);
	}



	@Transactional
	public void purge(Id id) {
		repository.delete(id);
	}



}