package com.bookexchange.service;

import com.bookexchange.model.Book;
import com.bookexchange.model.Exchange;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.ExchangeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ExchangeService {
    
    @Autowired
    private ExchangeRepository exchangeRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    public List<Exchange> getUserExchanges(Long userId) {
        List<Exchange> requestedExchanges = exchangeRepository.findByRequesterId(userId);
        List<Exchange> ownedExchanges = exchangeRepository.findByOwnerId(userId);
        
        requestedExchanges.addAll(ownedExchanges);
        return requestedExchanges;
    }
    
    @Transactional
    public Exchange createExchange(Exchange exchange) {
        Book book = bookRepository.findById(exchange.getBookId())
                .orElseThrow(() -> new RuntimeException("Book not found"));
        
        if (!book.getIsAvailable()) {
            throw new RuntimeException("Book is not available for exchange");
        }
        
        if (book.getOwnerId().equals(exchange.getRequesterId())) {
            throw new RuntimeException("You cannot request your own book");
        }
        
        exchange.setOwnerName(book.getOwnerName());
        exchange.setBookTitle(book.getTitle());
        
        return exchangeRepository.save(exchange);
    }
    
    @Transactional
    public Exchange updateExchangeStatus(Long exchangeId, Exchange.ExchangeStatus status) {
        Exchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));
        
        exchange.setStatus(status);
        
        if (status == Exchange.ExchangeStatus.ACCEPTED) {
            Book book = bookRepository.findById(exchange.getBookId()).orElse(null);
            if (book != null) {
                book.setIsAvailable(false);
                bookRepository.save(book);
            }
        }
        
        return exchangeRepository.save(exchange);
    }
    
    public Optional<Exchange> getExchangeById(Long id) {
        return exchangeRepository.findById(id);
    }
    
    public Long getCompletedExchangesCount() {
        return exchangeRepository.countCompletedExchanges();
    }
    
    public List<Object[]> getExchangeStatistics() {
        return exchangeRepository.getExchangeStatistics();
    }
}
