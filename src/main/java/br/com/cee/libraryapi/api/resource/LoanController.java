package br.com.cee.libraryapi.api.resource;

import br.com.cee.libraryapi.api.dto.LoanDTO;
import br.com.cee.libraryapi.api.dto.ReturnedLoanDTO;
import br.com.cee.libraryapi.model.entity.Book;
import br.com.cee.libraryapi.model.entity.Loan;
import br.com.cee.libraryapi.service.BookService;
import br.com.cee.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanDTO dto ) {
        Book book = bookService
                .getBookByIsbn(dto.getIsbn())
                .orElseThrow( () ->
                    new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for informed isbn."));

        Loan entity = Loan.builder()
                .book(book)
                .customer(dto.getCustomer())
                .loanDate(LocalDate.now()).build();

        Loan savedLoan = service.save(entity);

        return savedLoan.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(
            @PathVariable Long id,
            @RequestBody ReturnedLoanDTO dto) {
        Loan loan = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND) );
        loan.setReturned(dto.getReturned());
        service.update(loan);
    }


}
