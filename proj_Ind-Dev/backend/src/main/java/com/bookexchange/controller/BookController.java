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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000", 
             allowedHeaders = "*", 
             allowCredentials = "true", 
             methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Book management APIs")
public class BookController {
    
    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

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
    @Operation(summary = "Create book", description = "Create Book")
    public ResponseEntity<Book> createBook(@Valid @RequestBody Book book, @RequestHeader("Authorization") String authHeader,
                                                                          @RequestHeader("X-User-Id") Long userId,
                                                                          @RequestHeader("X-User-Name") String userName) {
        logger.info("Creating book for userId: {}", book.getOwnerId());
        book.setOwnerId(userId);
        book.setOwnerName(userName);
        Book createdBook = bookService.createBook(book);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Hide book", description = "Make book invisible, because they were traded")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        Optional<Book> optionalBook = bookService.getBookById(id);
        if (optionalBook.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Book book = optionalBook.get();
        book.setIsAvailable(false);
        bookService.createBook(book);  
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/me/{userId}")
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

}
