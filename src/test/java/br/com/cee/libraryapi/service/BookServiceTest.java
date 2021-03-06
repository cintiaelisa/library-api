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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

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
        when( repository.existsByIsbn(anyString()) ).thenReturn(false);
        when( repository.save(book)).thenReturn(
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
        when( repository.existsByIsbn(anyString()) ).thenReturn(true);

        //execução
        Throwable exception = Assertions.catchThrowable( () -> service.save(book));

        //verificação
        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");

        verify(repository, never()).save(book);

    }

    @Test
    @DisplayName("Deve obter um livro por Id")
    public void getByIdTest() {
        //cenário
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(book));
        
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
        when(repository.findById(id)).thenReturn(Optional.empty());

        //Execução
        Optional<Book> book = service.getById(id);

        //Verificações
        assertThat( book.isPresent() ).isFalse();

    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteBookTest() {
        //cenário
        Book book = createValidBook();

        //Execução
        assertDoesNotThrow( () -> repository.delete(book));


        //Verificação
        verify(repository, times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve lançar erro ao deletar livro inexistente.")
    public void deleteNullBook() {
        //cenário
        Book book = new Book();

        //Execução
        Throwable exception = Assertions.catchThrowable( () -> service.delete(book));

        //Verificação
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id can't be null");

        verify(repository, never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() {
        //cenário
        long id = 1L;
        Book updatingBook = Book.builder().id(id).build();

        //Simulação
        Book updatedBook = createValidBook();
        updatedBook.setId(id);
        when(repository.save(updatingBook)).thenReturn(updatedBook);

        //Execução
        Book book = service.update(updatingBook);

        //Verificação
        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        verify(repository, times(1)).save(updatingBook);


    }

    @Test
    @DisplayName("Deve lançar erro ao tentar atualizar livro inexistente.")
    public void updateNullBook() {
        //cenário
        Book book = new Book();

        //Execução
        Throwable exception = Assertions.catchThrowable( () -> service.update(book));

        //Verificação
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id can't be null");

        verify(repository, never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        //Cenário
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = Arrays.asList(book);
        Page<Book> page = new PageImpl<Book>(lista, pageRequest, 1);
        when( repository.findAll(any(Example.class), any(PageRequest.class))).thenReturn(page);

        //Execução
        Page<Book> result = service.find(book, pageRequest);

        //Verificações
        assertThat( result.getTotalElements() ).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve obter um livro pelo isbn")
    public void getBookByIsbn() {
        String isbn = "12131415";

        when( repository.findByIsbn(isbn)).thenReturn(Optional.of(Book.builder().id(1L).isbn(isbn).build()));

        Optional<Book> book = service.getBookByIsbn(isbn);

        assertThat(book.isPresent()).isTrue();
        assertThat(book.get().getId()).isEqualTo(1L);
        assertThat(book.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);

    }

    private Book createValidBook() {
        return Book.builder().isbn("12131415").author("Fulano").title("As aventuras").build();
    }
}
