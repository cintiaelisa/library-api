package br.com.cee.libraryapi.service;

import br.com.cee.libraryapi.api.dto.LoanFilterDTO;
import br.com.cee.libraryapi.model.entity.Book;
import br.com.cee.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);

    Optional<Loan> getById(Long id);

    Loan update(Loan loan);

    Page<Loan> find(LoanFilterDTO filter, Pageable pageable);

    Page<Loan> getLoandByBook(Book book, Pageable pageable);

    List<Loan> getAllLateLoans();
}
