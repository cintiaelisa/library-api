package br.com.cee.libraryapi.api.resource;

import br.com.cee.libraryapi.api.dto.BookDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    public void createdBookTest() throws Exception {

        BookDTO dto = BookDTO.builder().author("Arthur").title("As Aventuras").isbn("12131415").build();

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.ALL.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .contentType("");

        mvc
                .perform(request)
                .andExpect( status().isCreated() )
                .andExpect( jsonPath("id").isNotEmpty() )
                .andExpect( jsonPath("title").value(dto.getTitle()) )
                .andExpect( jsonPath("author").value(dto.getAuthor()) )
                .andExpect( jsonPath("isbn").value(dto.getIsbn()));
        ;

    }

    @Test
    @DisplayName("Deve lançar um erro de validação quando não houver dados suficientes para a criação do livro.")
    public void createInvalidBookTest() {

    }

}


