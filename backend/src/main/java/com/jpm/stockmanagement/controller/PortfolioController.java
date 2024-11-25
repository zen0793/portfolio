package com.jpm.stockmanagement.controller;

import com.jpm.stockmanagement.model.Stock;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import com.jpm.stockmanagement.service.PortfolioService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.Optional;


@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private static final Logger logger = LoggerFactory.getLogger(PortfolioController.class);


    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Operation(summary = "Add a stock to portfolio")
    @PostMapping("/add-stock")
    public ResponseEntity<?> addStock(@Valid @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Stock to be added",
            required = true, content = @Content(mediaType="application/json")) Stock stockRequest) {

        Optional<Stock> existingStock = portfolioService.stockExists(stockRequest);
        if (existingStock.isPresent()) {
            return handleExistingStock(existingStock.get(), stockRequest.getStockQuantity());
        } else {
            return handleNewStock(stockRequest);
        }
    }

    private ResponseEntity<?> handleExistingStock(Stock existingStock, int additionalQuantity) {
        updateQuantity(existingStock, additionalQuantity);
        portfolioService.addStock(existingStock);
        return ResponseEntity.ok(existingStock);
    }

    private ResponseEntity<?> handleNewStock(Stock stockRequest) {
        Stock newStock = portfolioService.fetchStockDetails(stockRequest);
        portfolioService.addStock(newStock);
        return ResponseEntity.ok(newStock);
    }

    @Operation(summary = "Delete a stock from portfolio")
    @DeleteMapping("/delete-stock/{symbol}")
    public ResponseEntity<String> deleteStock(@PathVariable String symbol) {
        portfolioService.deleteStock(symbol);
        return ResponseEntity.ok("Stock removed successfully!");
    }

    @Operation(summary = "Get list of stocks from portfolio")
    @GetMapping("/stocks")
    public ResponseEntity<List<Stock>> getStocks() {
        return ResponseEntity.ok(portfolioService.getStocks());
    }

    public void updateQuantity(Stock existingStock, int stockQuantity) {
        int quantityBefore = existingStock.getStockQuantity();
        existingStock.setStockQuantity(quantityBefore + stockQuantity);
    }

}
