package co.hooghly.commerce.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import co.hooghly.commerce.domain.Book;

@Repository
public interface BookRepository extends ElasticsearchRepository<Book, String> {

    Page<Book> findByAuthorsName(String name, Pageable pageable);

    @Query("{\"bool\": {\"must\": [{\"match\": {\"authors.name\": \"?0\"}}]}}")
    Page<Book> findByAuthorsNameUsingCustomQuery(String name, Pageable pageable);
}