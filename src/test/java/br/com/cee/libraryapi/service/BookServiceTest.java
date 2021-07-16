package br.com.cee.libraryapi.service;

import br.com.cee.libraryapi.exception.BusinessException;
import br.com.cee.libraryapi.model.entity.Book;
import br.com.cee.libraryapi.model.repository.BookRepository;
import br.com.cee.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest() {
        //cenário
        Book book = createValidBook();
        Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(false);
        Mockito.when( repository.save(book)).thenReturn(
                        Book.builder()
                        .id(1L)
                        .isbn("12131415")
                        .author("Fulano")
                        .title("As aventuras")
                        .build()
                );

        //execução
        Book savedBook = service.save(book);

        //verificação
        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("12131415");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
    }

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado")
    public void shouldNotSaveABookWithDuplicatedISBN(){
        //cenário
        Book book = createValidBook();
        Mockito.when( repository.existsByIsbn(Mockito.anyString()) ).thenReturn(true);

        //execução
        Throwable exception = Assertions.catchThrowable( () -> service.save(book));

        //verificação
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        Mockito.verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getByIdTest() {
        //cenário
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));
        
        //Execução
        Optional<Book> foundBook = service.getById(id);

        //Verificações
        assertThat( foundBook.isPresent() ).isTrue();
        assertThat( foundBook.get().getId() ).isEqualTo(id);
        assertThat( foundBook.get().getAuthor() ).isEqualTo(book.getAuthor());
        assertThat( foundBook.get().getTitle() ).isEqualTo(book.getTitle());
        assertThat( foundBook.get().getIsbn() ).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve retornar vazio ao buscar livro por id inexistente.")
    public void bookNotFoundByIdTest() {
        //cenário
        Long id = 1L;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //Execução
        Optional<Book> book = service.getById(id);

        //Verificações
        assertThat( book.isPresent() ).isFalse();

    }

    private Book createValidBook() {
        return Book.builder().isbn("12131415").author("Fulano").title("As aventuras").build();
    }
}
