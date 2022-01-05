package br.com.cee.libraryapi.model.repository;

import br.com.cee.libraryapi.model.entity.Book;
import br.com.cee.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static br.com.cee.libraryapi.model.repository.BookRepositoryTest.createNewBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
class LoanRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LoanRepository respository;

    @Test
    @DisplayName("Deve verificar se existe emprétimo não devolvido para o livro.")
    void existsByBookAndNotReturnedTest() {
        //cenário
        Loan loan = createAndPersistLoan(LocalDate.now());

        //execução
        boolean exists = respository.existsByBookAndNotReturned(loan.getBook());

        //verificação
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Deve buscar empréstimo pelo isbn do livro ou customer")
    void findByBookIsbnOrCustomer() {
        //cenário
        Loan loan = createAndPersistLoan(LocalDate.now());

        //execução
        Page<Loan> result = respository.findByBookIsbnOrCustomer("12131415", "Fulano", PageRequest.of(0, 10));

        //verificação
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter empréstimos cuja data de empréstimo for menor ou igual a três dias atras e não retornados.")
    void findByLoanDateLessThanAndNotReturnedTest() {
        Loan loan = createAndPersistLoan(LocalDate.now().minusDays(5));

        List<Loan> result = respository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);
    }

    @Test
    @DisplayName("Deve retornar vazio quando não houver empréstimos atrasados.")
    void notFindByLoanDateLessThanAndNotReturnedTest() {
        Loan loan = createAndPersistLoan(LocalDate.now());

        List<Loan> result = respository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).isEmpty();
    }

    public Loan createAndPersistLoan(LocalDate localDate) {

        Book book = createNewBook("12131415");
        entityManager.persist(book);

        Loan loan = Loan.builder().book(book).customer("Fulano").loanDate(localDate).build();
        entityManager.persist(loan);

        return loan;
    }
}
