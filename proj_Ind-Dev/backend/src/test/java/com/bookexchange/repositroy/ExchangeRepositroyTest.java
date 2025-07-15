package com.bookexchange.repositroy;

import com.bookexchange.repository.ExchangeRepository;
import com.bookexchange.model.Exchange;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ExchangeRepositoryTest {

    @Autowired
    private ExchangeRepository exchangeRepository;

    private Exchange exchange;

    @BeforeEach
    void setup() {
        exchangeRepository.deleteAll();
        exchange = new Exchange();
        exchange.setRequesterId(1L);
        exchange.setOwnerId(2L);
        exchange.setRequestedBookId(3L);
        exchange.setOfferedBookId(4L);
        exchangeRepository.save(exchange);
    }

    @Test
    void testFindByRequesterId() {
        List<Exchange> exchanges = exchangeRepository.findByRequesterId(1L);
        assertThat(exchanges).isNotEmpty();
    }

    @Test
    void testFindByOwnerId() {
        List<Exchange> exchanges = exchangeRepository.findByOwnerId(2L);
        assertThat(exchanges).isNotEmpty();
    }

    @Test
    void testCountCompletedExchanges() {
        Long count = exchangeRepository.countCompletedExchanges();
        assertThat(count).isNotNull();
    }
}
