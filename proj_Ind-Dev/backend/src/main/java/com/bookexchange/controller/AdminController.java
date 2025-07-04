package com.bookexchange.controller;

import com.bookexchange.service.BookService;
import com.bookexchange.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Admin", description = "Administrative APIs")
public class AdminController {
    
    @Autowired
    private BookService bookService;
    
    @Autowired
    private ExchangeService exchangeService;
    
    @GetMapping("/statistics")
    @Operation(summary = "Get platform statistics", 
               description = "Get comprehensive statistics about the platform")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        List<String> allBooks = bookService.getAllAvailableBooks()
                .stream()
                .map(book -> book.getTitle() + " by " + book.getAuthor())
                .toList();
        statistics.put("totalBooks", allBooks.size());
        statistics.put("books", allBooks);
        
        Long completedExchanges = exchangeService.getCompletedExchangesCount();
        List<Object[]> exchangeStats = exchangeService.getExchangeStatistics();
        
        statistics.put("completedExchanges", completedExchanges);
        statistics.put("exchangeStatistics", exchangeStats);
        
        List<String> genres = bookService.getAllGenres();
        statistics.put("availableGenres", genres);
        
        return ResponseEntity.ok(statistics);
    }
}
