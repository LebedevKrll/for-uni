package com.bookexchange.controller;

import com.bookexchange.model.Exchange;
import com.bookexchange.service.ExchangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@RestController
@RequestMapping("/api/exchanges")
@CrossOrigin(origins = "http://localhost:3000")
@Tag(name = "Exchanges", description = "Book exchange management APIs")
public class ExchangeController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);

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
        logger.info("CreateExchange called with: requestedBookId={}, offeredBookId={}, requesterId={}",
                exchange.getRequestedBookId(), exchange.getOfferedBookId(), exchange.getRequesterId());
        try {
            Exchange createdExchange = exchangeService.createExchange(exchange);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdExchange);
        } catch (RuntimeException e) {
            logger.error("Error creating exchange", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Get paginated exchanges", description = "For logs with pagination")
    public ResponseEntity<Page<Exchange>> getAllExchanges(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<Exchange> exchangesPage = exchangeService.getAllExchanges(pageable);
        return ResponseEntity.ok(exchangesPage);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exchange by ID", description = "Retrieve a specific exchange by its ID")
    public ResponseEntity<Exchange> getExchangeById(@PathVariable Long id) {
        Optional<Exchange> exchange = exchangeService.getExchangeById(id);
        return exchange.map(ResponseEntity::ok)
                       .orElse(ResponseEntity.notFound().build());
    }

}
