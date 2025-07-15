package com.bookexchange.service;

import com.bookexchange.model.Book;
import com.bookexchange.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book sampleBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sampleBook = new Book("Title", "Author", "Desc", "Genre", 1L, "Owner");
        sampleBook.setId(1L);
    }

    @Test
    void testGetAllAvailableBooks() {
        when(bookRepository.findByIsAvailableTrue()).thenReturn(List.of(sampleBook));

        List<Book> books = bookService.getAllAvailableBooks();

        assertThat(books).hasSize(1).contains(sampleBook);
        verify(bookRepository, times(1)).findByIsAvailableTrue();
    }

    @Test
    void testGetAvailableBooksWithFilters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(sampleBook));
        when(bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndGenreContainingIgnoreCaseAndIsAvailable(
                anyString(), anyString(), anyString(), eq(true), eq(pageable))).thenReturn(page);

        Page<Book> result = bookService.getAvailableBooksWithFilters("tit", "auth", "gen", pageable);

        assertThat(result.getContent()).contains(sampleBook);
        verify(bookRepository, times(1))
                .findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndGenreContainingIgnoreCaseAndIsAvailable(
                        "tit", "auth", "gen", true, pageable);
    }

    @Test
    void testGetBookById() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(sampleBook));

        Optional<Book> book = bookService.getBookById(1L);

        assertThat(book).isPresent().contains(sampleBook);
        verify(bookRepository).findById(1L);
    }

    @Test
    void testCreateBook() {
        when(bookRepository.save(sampleBook)).thenReturn(sampleBook);

        Book created = bookService.createBook(sampleBook);

        assertThat(created).isEqualTo(sampleBook);
        verify(bookRepository).save(sampleBook);
    }

    @Test
    void testGetUserBooks() {
        when(bookRepository.findByOwnerIdAndIsAvailableTrue(1L)).thenReturn(List.of(sampleBook));

        List<Book> books = bookService.getUserBooks(1L);

        assertThat(books).contains(sampleBook);
        verify(bookRepository).findByOwnerIdAndIsAvailableTrue(1L);
    }

    @Test
    void testGetAllGenres() {
        List<String> genres = List.of("Genre1", "Genre2");
        when(bookRepository.findAllAvailableGenres()).thenReturn(genres);

        List<String> result = bookService.getAllGenres();

        assertThat(result).isEqualTo(genres);
        verify(bookRepository).findAllAvailableGenres();
    }
}
