package com.bookexchange.service;

import com.bookexchange.model.Book;
import com.bookexchange.model.Exchange;
import com.bookexchange.repository.BookRepository;
import com.bookexchange.repository.ExchangeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExchangeServiceTest {

    @Mock
    private ExchangeRepository exchangeRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private ExchangeService exchangeService;

    private Book requestedBook;
    private Book offeredBook;
    private Exchange exchange;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        requestedBook = new Book();
        requestedBook.setId(1L);
        requestedBook.setOwnerId(2L);
        requestedBook.setIsAvailable(true);
        requestedBook.setTitle("Requested Book");
        requestedBook.setOwnerName("Owner A");

        offeredBook = new Book();
        offeredBook.setId(2L);
        offeredBook.setOwnerId(3L);
        offeredBook.setIsAvailable(true);
        offeredBook.setTitle("Offered Book");
        offeredBook.setOwnerName("Owner B");

        exchange = new Exchange();
        exchange.setRequestedBookId(requestedBook.getId());
        exchange.setOfferedBookId(offeredBook.getId());
        exchange.setRequesterId(offeredBook.getOwnerId());
    }

    @Test
    void createExchange_Success() {
        when(bookRepository.findById(requestedBook.getId())).thenReturn(Optional.of(requestedBook));
        when(bookRepository.findById(offeredBook.getId())).thenReturn(Optional.of(offeredBook));
        when(exchangeRepository.save(any(Exchange.class))).thenAnswer(i -> i.getArgument(0));

        Exchange created = exchangeService.createExchange(exchange);

        assertThat(created.getOwnerId()).isEqualTo(requestedBook.getOwnerId());
        assertThat(created.getRequestedBookTitle()).isEqualTo(requestedBook.getTitle());
        assertThat(created.getOfferedBookTitle()).isEqualTo(offeredBook.getTitle());

        verify(exchangeRepository).save(any(Exchange.class));
    }

    @Test
    void createExchange_RequestedBookNotAvailable_Throws() {
        requestedBook.setIsAvailable(false);
        when(bookRepository.findById(requestedBook.getId())).thenReturn(Optional.of(requestedBook));
        when(bookRepository.findById(offeredBook.getId())).thenReturn(Optional.of(offeredBook));

        assertThatThrownBy(() -> exchangeService.createExchange(exchange))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Requested book is not available");
    }

    @Test
    void createExchange_OfferBookNotOwnedByRequester_Throws() {
        offeredBook.setOwnerId(999L); // не совпадает с requesterId
        when(bookRepository.findById(requestedBook.getId())).thenReturn(Optional.of(requestedBook));
        when(bookRepository.findById(offeredBook.getId())).thenReturn(Optional.of(offeredBook));

        assertThatThrownBy(() -> exchangeService.createExchange(exchange))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("You can only offer your own book");
    }

    @Test
    void getUserExchanges_ReturnsCombinedList() {
        List<Exchange> requested = List.of(new Exchange());
        List<Exchange> owned = List.of(new Exchange());

        when(exchangeRepository.findByRequesterId(1L)).thenReturn(requested);
        when(exchangeRepository.findByOwnerId(1L)).thenReturn(owned);

        List<Exchange> result = exchangeService.getUserExchanges(1L);

        assertThat(result).hasSize(requested.size() + owned.size());
    }

    @Test
    void getAllExchanges_ReturnsPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Exchange> page = new PageImpl<>(List.of(new Exchange()));

        when(exchangeRepository.findAll(pageable)).thenReturn(page);

        Page<Exchange> result = exchangeService.getAllExchanges(pageable);

        assertThat(result.getContent()).isNotEmpty();
    }
}
