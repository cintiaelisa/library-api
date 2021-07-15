package br.com.cee.libraryapi.api.resource;

import br.com.cee.libraryapi.api.dto.BookDTO;
import br.com.cee.libraryapi.exception.BusinessException;
import br.com.cee.libraryapi.model.entity.Book;
import br.com.cee.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
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
    @DisplayName("Deve lançar um erro de validação quando não houver dados suficientes para a criação do livro.")
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
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn duplicado.")
    public void createBookWithDuplicatedIsbn() throws Exception {

        BookDTO dto = createBook();
        String json = new ObjectMapper().writeValueAsString(dto);
        String mensagemErro = "Isbn já cadastrado.";

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
    @DisplayName("Deve obter informações de um livro.")
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

        //Execução
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
    @DisplayName("Deve retornar resource not found quando o livro procurado não existir.")
    public void bookNotFoundTest() throws Exception {

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        mvc.perform(request)
                .andExpect( status().isNotFound() );

    }

    private BookDTO createBook() {
        return BookDTO.builder().author("Arthur").title("As Aventuras").isbn("12131415").build();
    }

}


