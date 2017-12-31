package co.hooghly.commerce.business;

import java.io.Serializable;
import java.util.List;

public interface SalesManagerEntityService<K extends Serializable & Comparable<K>, E extends co.hooghly.commerce.domain.SalesManagerEntity<K, ?>>
		extends TransactionalAspectAwareService {

	E save(E entity);

	void delete(E entity);

	List<E> list();

	Long count();

}
