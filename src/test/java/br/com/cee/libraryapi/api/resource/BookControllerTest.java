package br.com.cee.libraryapi.api.resource;

import br.com.cee.libraryapi.api.dto.BookDTO;
import br.com.cee.libraryapi.exception.BusinessException;
import br.com.cee.libraryapi.model.entity.Book;
import br.com.cee.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createdBookTest() throws Exception {

        BookDTO dto = createBook();
        Book savedBook = Book.builder().id(10L).author("Arthur").title("As Aventuras").isbn("12131415").build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isCreated() )
                .andExpect( jsonPath("id").isNotEmpty() )
                .andExpect( jsonPath("id").value( 10L ) )
                .andExpect( jsonPath("title").value(dto.getTitle()) )
                .andExpect( jsonPath("author").value(dto.getAuthor()) )
                .andExpect( jsonPath("isbn").value(dto.getIsbn()) );
        ;

    }

    @Test
    @DisplayName("Deve lan??ar um erro de valida????o quando n??o houver dados suficientes para a cria????o do livro.")
    public void createInvalidBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(3)));

    }

    @Test
    @DisplayName("Deve lan??ar erro ao tentar cadastrar um livro com isbn duplicado.")
    public void createBookWithDuplicatedIsbn() throws Exception {

        BookDTO dto = createBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Isbn j?? cadastrado.";

        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(mensagemErro));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform( request )
                .andExpect( status().isBadRequest() )
                .andExpect( jsonPath("errors", hasSize(1)))
                .andExpect( jsonPath("errors[0]").value(mensagemErro));

    }

    @Test
    @DisplayName("Deve obter informa????es de um livro.")
    public void getBookDetailsTest() throws Exception {
        //cenario
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .title(createBook().getTitle())
                .author(createBook().getAuthor())
                .isbn(createBook().getIsbn())
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        //Execu????o
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect( status().isOk() )
                .andExpect( jsonPath("id").value( id ) )
                .andExpect( jsonPath("title").value(createBook().getTitle()) )
                .andExpect( jsonPath("author").value(createBook().getAuthor()) )
                .andExpect( jsonPath("isbn").value(createBook().getIsbn()) );

    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro procurado n??o existir.")
    public void bookNotFoundTest() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        mvc.perform(request)
                .andExpect( status().isNotFound() );

    }

    @Test
    @DisplayName("Deve deletar um livro.")
    public void deleteBookTest() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mvc.perform( request )
                .andExpect( status().isNoContent() );

    }

    @Test
    @DisplayName("Deve retornar resource not found quando n??o encontrar o livro a deletar.")
    public void deleteNonexistentBookTest() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1));

        mvc.perform( request )
                .andExpect( status().isNotFound() );

    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void updateBookTest() throws Exception {

        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createBook());

        Book updatingBook = Book.builder().id(1L).title("some title").author("some author").isbn("12131415").build();
        Book updatedBook = Book.builder().author("Arthur").title("As Aventuras").isbn("12131415").build();

        BDDMockito.given(service.getById( id ))
                .willReturn( Optional.of( updatingBook ));

        BDDMockito.given(service.update( updatingBook ))
                .willReturn( updatedBook );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);;

        mvc.perform( request )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("id").value( id ) )
                .andExpect( jsonPath("title").value(createBook().getTitle()) )
                .andExpect( jsonPath("author").value(createBook().getAuthor()) )
                .andExpect( jsonPath("isbn").value(createBook().getIsbn()) );
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente")
    public void updateNonexistentBookTest() throws Exception {

        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createBook());

        BDDMockito.given(service.getById( id ))
                .willReturn( Optional.empty() );

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);;

        mvc.perform( request )
                .andExpect( status().isNotFound() );
    }
    @Test
    @DisplayName("Deve filtrar livros.")
    public void findBookTest() throws Exception {

        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .title(createBook().getTitle())
                .author(createBook().getAuthor())
                .isbn(createBook().getIsbn())
                .build();

        BDDMockito.given( service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)) )
            .willReturn( new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0, 100), 1) );

        String queryString = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform( request )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("content", Matchers.hasSize(1)) )
                .andExpect( jsonPath("totalElements").value(1) )
                .andExpect( jsonPath("pageable.pageSize").value(100) )
                .andExpect( jsonPath("pageable.pageNumber").value(0));


    }

    private BookDTO createBook() {
        return BookDTO.builder().author("Arthur").title("As Aventuras").isbn("12131415").build();
    }

}


