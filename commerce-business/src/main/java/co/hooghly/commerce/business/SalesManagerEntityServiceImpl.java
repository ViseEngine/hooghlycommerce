package co.hooghly.commerce.business;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.hooghly.commerce.domain.SalesManagerEntity;

/**
 * @param <T> entity type
 */
public abstract class SalesManagerEntityServiceImpl<K extends Serializable & Comparable<K>, E extends SalesManagerEntity<K, ?>>
	implements SalesManagerEntityService<K, E> {
	
	private Class<E> objectClass;


    private JpaRepository<E, K> repository;

	@SuppressWarnings("unchecked")
	public SalesManagerEntityServiceImpl(JpaRepository<E, K> repository) {
		ParameterizedType genericSuperclass = (ParameterizedType) getClass().getGenericSuperclass();
		this.objectClass = (Class<E>) genericSuperclass.getActualTypeArguments()[1];
		this.repository = repository;
	}
	
	protected final Class<E> getObjectClass() {
		return objectClass;
	}


	public E getById(K id) {
		return repository.findOne(id);
	}

	
	public E save(E entity)  {
		return repository.save(entity);
	}
	
	
	public void create(E entity)  {
		save(entity);
	}

	
	
	public void update(E entity)  {
		save(entity);
	}
	

	public void delete(E entity)  {
		repository.delete(entity);
	}
	
	
	public void flush() {
		repository.flush();
	}
	

	
	public List<E> list() {
		return repository.findAll();
	}
	

	public Long count() {
		return repository.count();
	}

}