package br.com.cee.libraryapi.service.impl;

import br.com.cee.libraryapi.model.entity.Book;
import br.com.cee.libraryapi.model.repository.BookRepository;
import br.com.cee.libraryapi.service.BookService;

public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        return repository.save(book);
    }
}
