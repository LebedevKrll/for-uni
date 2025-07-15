package com.bookexchange.controller;

import com.bookexchange.model.Exchange;
import com.bookexchange.repository.ExchangeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExchangeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ExchangeRepository exchangeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Exchange exchange;

    @BeforeAll
    void setup() {
        exchangeRepository.deleteAll();
        exchange = new Exchange();
        exchange.setRequestedBookId(1L);
        exchange.setOfferedBookId(2L);
        exchange.setRequesterId(3L);
        exchange.setRequesterName("Requester");
        exchange.setOwnerId(4L);
        exchange.setOwnerName("Owner");
        exchangeRepository.save(exchange);
    }

    @AfterAll
    void cleanup() {
        exchangeRepository.deleteAll();
    }

    @Test
    void testCreateExchange_Success() throws Exception {
        Exchange newExchange = new Exchange();
        newExchange.setRequestedBookId(10L);
        newExchange.setOfferedBookId(20L);

        mockMvc.perform(post("/api/exchanges")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-User-Id", 20L)
                .header("X-User-Name", "User20")
                .content(objectMapper.writeValueAsString(newExchange)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetExchangeById_Found() throws Exception {
        mockMvc.perform(get("/api/exchanges/{id}", exchange.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requesterName", is(exchange.getRequesterName())));
    }

    @Test
    void testGetExchangeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/exchanges/{id}", 9999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllExchanges() throws Exception {
        mockMvc.perform(get("/api/exchanges")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", not(empty())));
    }
}
