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

        // Получаем все доступные книги
        List<?> allBooksList = bookService.getAllAvailableBooks();
        List<String> allBooks = allBooksList == null ? List.of() :
            allBooksList.stream()
                .map(book -> {
                    // Предполагается, что book имеет методы getTitle() и getAuthor()
                    // Если book — это объект Book, приведите тип и вызовите методы
                    try {
                        var b = (com.bookexchange.model.Book) book;
                        return b.getTitle() + " by " + b.getAuthor();
                    } catch (ClassCastException e) {
                        return "Unknown book";
                    }
                })
                .toList();

        statistics.put("totalBooks", allBooks.size());
        statistics.put("books", allBooks);

        // Количество завершённых обменов
        Long completedExchanges = exchangeService.getCompletedExchangesCount();
        statistics.put("completedExchanges", completedExchanges != null ? completedExchanges : 0L);

        // Статистика по статусам обменов
        List<Object[]> exchangeStats = exchangeService.getExchangeStatistics();
        statistics.put("exchangeStatistics", exchangeStats != null ? exchangeStats : List.of());

        // Список жанров
        List<String> genres = bookService.getAllGenres();
        statistics.put("availableGenres", genres != null ? genres : List.of());

        return ResponseEntity.ok(statistics);
    }
}
