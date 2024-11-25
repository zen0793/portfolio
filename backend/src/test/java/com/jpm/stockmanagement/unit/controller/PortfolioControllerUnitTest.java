package com.jpm.stockmanagement.unit.controller;

import com.jpm.stockmanagement.controller.PortfolioController;
import com.jpm.stockmanagement.model.Stock;
import com.jpm.stockmanagement.service.PortfolioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PortfolioController.class)
public class PortfolioControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PortfolioService portfolioService;

    @Test
    void testAddStock() throws Exception {
        Stock stock = new Stock("AAPL", 10, 150);
        doNothing().when(portfolioService).addStock(stock);

        mockMvc.perform(post("/api/portfolio/add-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"stockSymbol\": \"AAPL\", \"stockQuantity\": 10, \"purchasePrice\": 150.0}"))
                .andExpect(status().isOk());
    }

    @Test
    void testAddStock_InvalidSymbol() throws Exception {
        Stock stock = new Stock(null, 10, 0);
        doThrow(new RuntimeException("Invalid stock symbol")).when(portfolioService).addStock(stock);

        mockMvc.perform(post("/api/portfolio/add-stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testDeleteStock() throws Exception {
        doNothing().when(portfolioService).deleteStock("AAPL");

        mockMvc.perform(delete("/api/portfolio/delete-stock/AAPL"))
                .andExpect(status().isOk())
                .andExpect(content().string("Stock removed successfully!"));
    }

    @Test
    void testGetStocks() throws Exception {
        List<Stock> stockList = new ArrayList<>();
        stockList.add(new Stock("AAPL", 1, 210));
        stockList.add(new Stock("IBM", 10, 200));
        doReturn(stockList).when(portfolioService).getStocks();

        mockMvc.perform(get("/api/portfolio/stocks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].stockSymbol").value("AAPL"))
                .andExpect(jsonPath("$[0].stockQuantity").value(1))
                .andExpect(jsonPath("$[0].purchasePrice").value(210))
                .andExpect(jsonPath("$[1].stockSymbol").value("IBM"))
                .andExpect(jsonPath("$[1].stockQuantity").value(10))
                .andExpect(jsonPath("$[1].purchasePrice").value(200));
    }
}






