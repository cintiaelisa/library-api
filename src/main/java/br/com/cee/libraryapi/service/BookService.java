package br.com.cee.libraryapi.service;

import br.com.cee.libraryapi.api.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

    public Book save(Book book);
}