package com.bookexchange.repositroy;

import com.bookexchange.repository.BookRepository;
import com.bookexchange.model.Book;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book book;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
        book = new Book("Title", "Author", "Desc", "Genre", 1L, "Owner");
        book.setIsAvailable(true);
        bookRepository.save(book);
    }

    @Test
    void testFindByIsAvailableTrue() {
        List<Book> books = bookRepository.findByIsAvailableTrue();
        assertThat(books).isNotEmpty();
        assertThat(books.get(0).getTitle()).isEqualTo("Title");
    }

    @Test
    void testFindByOwnerIdAndIsAvailableTrue() {
        List<Book> books = bookRepository.findByOwnerIdAndIsAvailableTrue(1L);
        assertThat(books).isNotEmpty();
        assertThat(books.get(0).getOwnerId()).isEqualTo(1L);
    }

    @Test
    void testFindByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndGenreContainingIgnoreCaseAndIsAvailable() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = bookRepository.findByTitleContainingIgnoreCaseAndAuthorContainingIgnoreCaseAndGenreContainingIgnoreCaseAndIsAvailable(
                "tit", "auth", "gen", true, pageable);
        assertThat(page.getContent()).isNotEmpty();
    }

    @Test
    void testFindAllAvailableGenres() {
        List<String> genres = bookRepository.findAllAvailableGenres();
        assertThat(genres).contains("Genre");
    }
}
