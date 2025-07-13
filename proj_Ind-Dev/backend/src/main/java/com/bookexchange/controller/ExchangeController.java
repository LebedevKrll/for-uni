package com.bookexchange.controller;

import com.bookexchange.model.Exchange;
import com.bookexchange.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/exchanges")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Exchanges", description = "Book exchange management APIs")
public class ExchangeController {

    @Autowired
    private ExchangeService exchangeService;

    @PostMapping
    @Operation(summary = "Create exchange request",
               description = "Create a new book exchange request")
    public ResponseEntity<?> createExchange(@Valid @RequestBody Exchange exchange,
                                            @RequestHeader("X-User-Id") Long userId,
                                            @RequestHeader("X-User-Name") String userName) {
        exchange.setRequesterId(userId);
        exchange.setRequesterName(userName);

        try {
            Exchange createdExchange = exchangeService.createExchange(exchange);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExchange);
        } catch (RuntimeException e) {
            // Возвращаем сообщение ошибки для удобства фронтенда
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exchange by ID", description = "Retrieve a specific exchange by its ID")
    public ResponseEntity<Exchange> getExchangeById(@PathVariable Long id) {
        Optional<Exchange> exchange = exchangeService.getExchangeById(id);
        return exchange.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

    // Дополнительно можно добавить метод для получения всех обменов пользователя
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get exchanges for user", description = "Get all exchanges where user is requester or owner")
    public ResponseEntity<List<Exchange>> getUserExchanges(@PathVariable Long userId) {
        List<Exchange> exchanges = exchangeService.getUserExchanges(userId);
        return ResponseEntity.ok(exchanges);
    }

    // Можно добавить метод для обновления статуса обмена, если нужно
    @PutMapping("/{id}/status")
    @Operation(summary = "Update exchange status", description = "Update status of an exchange")
    public ResponseEntity<?> updateExchangeStatus(@PathVariable Long id,
                                                  @RequestParam Exchange.ExchangeStatus status) {
        try {
            Exchange updatedExchange = exchangeService.updateExchangeStatus(id, status);
            return ResponseEntity.ok(updatedExchange);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
