package br.com.cee.libraryapi.service;

import br.com.cee.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BookService {

    public Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);
}
