package co.hooghly.commerce.orderflo.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


@NoRepositoryBean
public interface BaseRepository<T, Id extends Serializable> extends JpaRepository<T, Id>{
	//List<T> findAll(SearchRequest searchRequest);
	//List<T> findAllByDeleted(boolean deleted);
}