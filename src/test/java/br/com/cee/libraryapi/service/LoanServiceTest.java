package br.com.cee.libraryapi.service;

import br.com.cee.libraryapi.model.entity.Book;
import br.com.cee.libraryapi.model.entity.Loan;
import br.com.cee.libraryapi.model.repository.LoanRepository;
import br.com.cee.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
        Loan savingLoan = Loan.builder()
                .book(Book.builder().id(1L).build())
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        Loan savedLoan = Loan.builder()
                .book(Book.builder().id(1L).build())
                .customer("Fulano")
                .loanDate(LocalDate.now())
                .build();

        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getId()).isEqualTo(savedLoan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getId()).isEqualTo(savedLoan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getId()).isEqualTo(savedLoan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

}
