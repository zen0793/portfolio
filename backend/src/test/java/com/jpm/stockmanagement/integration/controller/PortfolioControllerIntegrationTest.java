package com.jpm.stockmanagement.integration.controller;

import com.jpm.stockmanagement.model.Stock;
import com.jpm.stockmanagement.repository.PortfolioRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PortfolioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PortfolioRepository portfolioRepository;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testAddNewStock() throws Exception {
        String requestBody = """
                {
                    "stockSymbol": "AAPL",
                    "stockQuantity": 10
                }
                """;

        mockServer.expect(requestTo("https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=AAPL&apikey=8EY8JUEZ7Z21HI5P"))
                .andRespond(withSuccess("""
                        {
                            "Global Quote": {
                                "01. symbol": "AAPL",
                                "05. price": "150"
                            }
                        }
                        """, MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/api/portfolio/add-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockSymbol").value("AAPL"))
                .andExpect(jsonPath("$.stockQuantity").value(10))
                .andExpect(jsonPath("$.purchasePrice").value(150));

        // Database check
        Optional<Stock> savedStock = portfolioRepository.findById("AAPL");
        assertTrue(savedStock.isPresent());
        assertEquals("AAPL", savedStock.get().getStockSymbol());
        assertEquals(10, savedStock.get().getStockQuantity());
        assertEquals(150, savedStock.get().getPurchasePrice());
    }

    @Test
    void testAddExistingStock() throws Exception {
        Stock existingStock = new Stock("AAPL", 5, 150);
        portfolioRepository.save(existingStock);

        String requestBody = """
                {
                    "stockSymbol": "AAPL",
                    "stockQuantity": 5
                }
                """;

        mockMvc.perform(post("/api/portfolio/add-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockSymbol").value("AAPL"))
                .andExpect(jsonPath("$.stockQuantity").value(10));

        // Database check
        Optional<Stock> updatedStock = portfolioRepository.findById("AAPL");
        assertTrue(updatedStock.isPresent());
        assertEquals(10, updatedStock.get().getStockQuantity());
    }
}
