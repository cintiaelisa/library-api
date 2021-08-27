package br.com.cee.libraryapi.service;

import br.com.cee.libraryapi.api.resource.BookController;
import br.com.cee.libraryapi.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);
}
