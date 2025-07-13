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
        Book requestedBook = bookRepository.findById(exchange.getRequestedBookId())
                .orElseThrow(() -> new RuntimeException("Requested book not found"));

        Book offeredBook = bookRepository.findById(exchange.getOfferedBookId())
                .orElseThrow(() -> new RuntimeException("Offered book not found"));

        if (!requestedBook.getIsAvailable()) {
            throw new RuntimeException("Requested book is not available for exchange");
        }
        if (!offeredBook.getIsAvailable()) {
            throw new RuntimeException("Offered book is not available for exchange");
        }

        if (requestedBook.getOwnerId().equals(exchange.getRequesterId())) {
            throw new RuntimeException("You cannot request your own book");
        }
        if (!offeredBook.getOwnerId().equals(exchange.getRequesterId())) {
            throw new RuntimeException("You can only offer your own book for exchange");
        }

        exchange.setOwnerId(requestedBook.getOwnerId());
        exchange.setOwnerName(requestedBook.getOwnerName());

        exchange.setRequestedBookTitle(requestedBook.getTitle());
        exchange.setOfferedBookTitle(offeredBook.getTitle());

        return exchangeRepository.save(exchange);
    }

    @Transactional
    public Exchange updateExchangeStatus(Long exchangeId, Exchange.ExchangeStatus status) {
        Exchange exchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new RuntimeException("Exchange not found"));

        exchange.setStatus(status);

        if (status == Exchange.ExchangeStatus.ACCEPTED) {
            Book requestedBook = bookRepository.findById(exchange.getRequestedBookId()).orElse(null);
            Book offeredBook = bookRepository.findById(exchange.getOfferedBookId()).orElse(null);

            if (requestedBook != null) {
                requestedBook.setIsAvailable(false);
                bookRepository.save(requestedBook);
            }
            if (offeredBook != null) {
                offeredBook.setIsAvailable(false);
                bookRepository.save(offeredBook);
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
