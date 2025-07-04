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
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user exchanges", description = "Get all exchanges for a specific user")
    public ResponseEntity<List<Exchange>> getUserExchanges(@PathVariable Long userId) {
        List<Exchange> exchanges = exchangeService.getUserExchanges(userId);
        return ResponseEntity.ok(exchanges);
    }
    
    @PostMapping
    @Operation(summary = "Create exchange request", 
               description = "Create a new book exchange request")
    public ResponseEntity<Exchange> createExchange(@Valid @RequestBody Exchange exchange,
                                                 @RequestHeader("X-User-Id") Long userId,
                                                 @RequestHeader("X-User-Name") String userName) {
        exchange.setRequesterId(userId);
        exchange.setRequesterName(userName);
        
        try {
            Exchange createdExchange = exchangeService.createExchange(exchange);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExchange);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{id}/status")
    @Operation(summary = "Update exchange status", 
               description = "Update the status of an exchange request")
    public ResponseEntity<Exchange> updateExchangeStatus(@PathVariable Long id,
                                                       @RequestParam Exchange.ExchangeStatus status) {
        try {
            Exchange updatedExchange = exchangeService.updateExchangeStatus(id, status);
            return ResponseEntity.ok(updatedExchange);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get exchange by ID", description = "Retrieve a specific exchange by its ID")
    public ResponseEntity<Exchange> getExchangeById(@PathVariable Long id) {
        Optional<Exchange> exchange = exchangeService.getExchangeById(id);
        return exchange.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }
}
