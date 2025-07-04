package com.bookexchange.controller;

import com.bookexchange.model.Book;
import com.bookexchange.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;
import com.bookexchange.util.JWTUtil;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book management APIs")
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping
    @Operation(summary = "Get available books with filters", 
               description = "Retrieve paginated list of available books with optional filters")
    public ResponseEntity<Page<Book>> getAvailableBooks(
            @Parameter(description = "Book title filter") @RequestParam(required = false) String title,
            @Parameter(description = "Author filter") @RequestParam(required = false) String author,
            @Parameter(description = "Genre filter") @RequestParam(required = false) String genre,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> books = bookService.getAvailableBooksWithFilters(title, author, genre, pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get book by ID", description = "Retrieve a specific book by its ID")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookService.getBookById(id);
        return book.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @Operation(summary = "Create new book", description = "Add a new book to the exchange platform")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book, @RequestHeader("Authorization") String authHeader) {
        System.out.println("This is a log message to standard output.");
        String token = authHeader.replace("Bearer ", "");
        String userName = JWTUtil.extractUsername(token);
        Long userId = JWTUtil.extractUserId(token);
        book.setOwnerId(userId);
        book.setOwnerName(userName);
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update book", description = "Update an existing book")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, 
                                         @Valid @RequestBody Book bookDetails) {
        try {
            Book updatedBook = bookService.updateBook(id, bookDetails);
            return ResponseEntity.ok(updatedBook);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete book", description = "Remove a book from the platform")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user books", description = "Get all books owned by a specific user")
    public ResponseEntity<List<Book>> getUserBooks(@PathVariable Long userId) {
        List<Book> books = bookService.getUserBooks(userId);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/genres")
    @Operation(summary = "Get all genres", description = "Get list of all available book genres")
    public ResponseEntity<List<String>> getAllGenres() {
        List<String> genres = bookService.getAllGenres();
        return ResponseEntity.ok(genres);
    }
    
    @GetMapping("/recommendations")
    @Operation(summary = "Get book recommendations", 
               description = "Get book recommendations based on genre")
    public ResponseEntity<List<Book>> getRecommendations(@RequestParam String genre) {
        List<Book> recommendations = bookService.getRecommendationsByGenre(genre);
        return ResponseEntity.ok(recommendations);
    }
}
