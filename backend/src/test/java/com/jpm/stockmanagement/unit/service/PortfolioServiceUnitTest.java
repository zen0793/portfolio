package com.jpm.stockmanagement.unit.service;

import com.jpm.stockmanagement.exceptions.FetchStockDetailsException;
import com.jpm.stockmanagement.model.AlphaVantageResponse;
import com.jpm.stockmanagement.model.Stock;
import com.jpm.stockmanagement.repository.PortfolioRepository;
import com.jpm.stockmanagement.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PortfolioServiceUnitTest {

    @InjectMocks
    private PortfolioService portfolioService;

    @Mock
    private PortfolioRepository portfolioRepository;

    @Mock
    private RestTemplate restTemplate;

    private static final String BASE_URL = "https://www.alphavantage.co/query";
    private static final String API_KEY = "8EY8JUEZ7Z21HI5P";
    private static final Stock testStock = new Stock("AAPL", 10, 0);

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(portfolioService, "baseUrl", BASE_URL);
        ReflectionTestUtils.setField(portfolioService, "apiKey", API_KEY);
    }

    @Test
    void testFetchStockDetails_Success() {
        AlphaVantageResponse.GlobalQuote globalQuote = new AlphaVantageResponse.GlobalQuote();
        globalQuote.setSymbol("AAPL");
        globalQuote.setPrice(150);

        AlphaVantageResponse response = new AlphaVantageResponse();
        response.setGlobalQuote(globalQuote);

        String expectedUrl = UriComponentsBuilder
                .fromHttpUrl(BASE_URL)
                .queryParam("function", "GLOBAL_QUOTE")
                .queryParam("symbol", testStock.getStockSymbol())
                .queryParam("apikey", API_KEY)
                .toUriString();

        when(restTemplate.getForObject(eq(expectedUrl), eq(AlphaVantageResponse.class)))
                .thenReturn(response);

        Stock fetchedStock = portfolioService.fetchStockDetails(testStock);

        assertEquals("AAPL", fetchedStock.getStockSymbol());
        assertEquals(150, fetchedStock.getPurchasePrice());
        assertTrue(fetchedStock.getPurchasePrice() > 0);

        verify(restTemplate, times(1)).getForObject(eq(expectedUrl), eq(AlphaVantageResponse.class));
    }

    @Test
    void testFetchStockDetails_Failure() {
        Stock testStock = new Stock(null, 10, 0);

        when(restTemplate.getForObject(anyString(), eq(AlphaVantageResponse.class)))
                .thenReturn(null);

        Exception exception = assertThrows(FetchStockDetailsException.class, () -> {
            portfolioService.fetchStockDetails(testStock);
        });

        assertEquals("Failed to fetch stock details for: null", exception.getMessage());
        verify(restTemplate, times(1)).getForObject(anyString(), eq(AlphaVantageResponse.class));
    }

    @Test
    void testStockExists() {
        when(portfolioRepository.findById("AAPL")).thenReturn(Optional.of(testStock));

        Optional<Stock> result = portfolioService.stockExists(testStock);

        assertTrue(result.isPresent());
        assertEquals(testStock, result.get());
        verify(portfolioRepository, times(1)).findById("AAPL");
    }

    @Test
    void testAddStock() {
        portfolioService.addStock(testStock);
        verify(portfolioRepository, times(1)).save(testStock);
    }

    @Test
    void testDeleteStock() {
        when(portfolioRepository.findById("AAPL")).thenReturn(Optional.of(testStock));

        portfolioService.deleteStock("AAPL");
        verify(portfolioRepository, times(1)).delete(testStock);
        verify(portfolioRepository, times(1)).findById("AAPL");
    }

}


