package com.bookexchange.service;

import com.bookexchange.model.Book;
import com.bookexchange.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<Book> getAllAvailableBooks() {
        return bookRepository.findByIsAvailableTrue();
    }
    
    public Page<Book> getAvailableBooksWithFilters(String title, String author, String genre, Pageable pageable) {
        return bookRepository.findByIsAvailableTrue();
    }
    
    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }
    
    public Book createBook(Book book) {
        return bookRepository.save(book);
    }
    
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setDescription(bookDetails.getDescription());
        book.setGenre(bookDetails.getGenre());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setIsAvailable(bookDetails.getIsAvailable());
        
        return bookRepository.save(book);
    }
    
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
    
    public List<Book> getUserBooks(Long userId) {
        return bookRepository.findByOwnerIdAndIsAvailableTrue(userId);
    }
    
    public List<String> getAllGenres() {
        return bookRepository.findAllAvailableGenres();
    }
    
    public List<Book> getRecommendationsByGenre(String genre) {
        return bookRepository.findByGenreAndIsAvailableTrueOrderByCreatedAtDesc(genre);
    }
}
