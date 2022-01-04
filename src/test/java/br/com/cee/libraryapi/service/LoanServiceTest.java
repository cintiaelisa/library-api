package br.com.cee.libraryapi.service;

import br.com.cee.libraryapi.exception.BusinessException;
import br.com.cee.libraryapi.model.entity.Book;
import br.com.cee.libraryapi.model.entity.Loan;
import br.com.cee.libraryapi.model.repository.LoanRepository;
import br.com.cee.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    private LoanService service;

    @MockBean
    private LoanRepository repository;

    @BeforeEach
    public void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest() {
        Book book = Book.builder().id(1L).build();

        Loan savingLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .book(book)
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(false);
        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook()).isEqualTo(savedLoan.getBook());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao salvar um empréstimo com livro já emprestado.")
    public void loanedBookSaveTest() {

        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        Loan savingLoan = Loan.builder()
                .book(Book.builder().id(1L).build())
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        when(repository.existsByBookAndNotReturned(book)).thenReturn(true);

        Throwable exception = catchThrowable( () -> service.save(savingLoan));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Book already loaned.");

        verify(repository, never()).save(savingLoan);

    }

    @Test
    @DisplayName("Deve obter as informações de um empréstimo pelo ID.")
    public void getLoanDetailsTest() {
        Long id = 1L;

        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id) ).thenReturn(Optional.of(loan));

        Optional<Loan> result = service.getById(id);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(id);
        assertThat(result.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(result.get().getBook()).isEqualTo(loan.getBook());
        assertThat(result.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository).findById(id);

    }
    @Test
    @DisplayName("")
    public void updateLoanTest() {
        Loan loan = createLoan();
        loan.setId(1L);
        loan.setReturned(true);

        when( repository.save(loan) ).thenReturn( loan );

        Loan updatedLoan = service.update(loan);

        assertThat(updatedLoan.getReturned()).isTrue();
        verify(repository).save(loan);
    }

    public static Loan createLoan() {
        Book book = Book.builder().id(1L).build();
        String customer = "Fulano";

        return Loan.builder()
                .book(Book.builder().id(1L).build())
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();
    }

}
