package co.hooghly.commerce.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import co.hooghly.commerce.domain.Book;
import co.hooghly.commerce.repository.BookRepository;

@Service
public class BookService  {

    private final BookRepository articleRepository;
    
    
    public BookService(BookRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    
    public Book save(Book article) {
        return articleRepository.save(article);
    }

    
    public Book findOne(String id) {
        return articleRepository.findOne(id);
    }

    
    public Iterable<Book> findAll() {
        return articleRepository.findAll();
    }

    
    public Page<Book> findByAuthorName(String name, Pageable pageable) {
        return articleRepository.findByAuthorsName(name, pageable);
    }

    
    public Page<Book> findByAuthorNameUsingCustomQuery(String name, Pageable pageable) {
        return articleRepository.findByAuthorsNameUsingCustomQuery(name, pageable);
    }

    
    public long count() {
        return articleRepository.count();
    }

    
    public void delete(Book article) {
        articleRepository.delete(article);
    }
    
    public void deleteAll() {
    	articleRepository.deleteAll();
    }
}