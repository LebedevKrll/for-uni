package com.bookexchange.controller;

import com.bookexchange.model.Book;
import com.bookexchange.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class BookControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Book book;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
        book = new Book("Test Title", "Test Author", "Desc", "Fiction", 1L, "OwnerName");
        book.setIsAvailable(true);
        bookRepository.save(book);
    }

    @AfterAll
    void cleanup() {
        bookRepository.deleteAll();
    }

    @Test
    void testGetAvailableBooks() throws Exception {
        mockMvc.perform(get("/api/books")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[0].title", is(book.getTitle())));
    }

    @Test
    void testGetBookById() throws Exception {
        mockMvc.perform(get("/api/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is(book.getTitle())))
                .andExpect(jsonPath("$.author", is(book.getAuthor())));
    }

    @Test
    void testCreateBook() throws Exception {
        Book newBook = new Book("New Book", "New Author", "Desc", "Drama", 2L, "User2");

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer fake-token")
                .header("X-User-Id", newBook.getOwnerId())
                .header("X-User-Name", newBook.getOwnerName())
                .content(objectMapper.writeValueAsString(newBook)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is(newBook.getTitle())))
                .andExpect(jsonPath("$.ownerId", is(newBook.getOwnerId().intValue())));
    }

    @Test
    void testHideBook() throws Exception {
        mockMvc.perform(put("/api/books/{id}", book.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/books/{id}", book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isAvailable", is(false)));
    }

    @Test
    void testGetUserBooks() throws Exception {
        mockMvc.perform(get("/api/books/me/{userId}", book.getOwnerId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ownerId", is(book.getOwnerId().intValue())));
    }
}
