package com.jpm.stockmanagement.service;

import com.jpm.stockmanagement.exceptions.FetchStockDetailsException;
import com.jpm.stockmanagement.model.AlphaVantageResponse;
import com.jpm.stockmanagement.model.Stock;
import com.jpm.stockmanagement.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class PortfolioService {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioService.class);

    @Value("${alpha.api.base-url}")
    private String baseUrl;

    @Value("${alpha.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final PortfolioRepository portfolioRepository;

    public PortfolioService(RestTemplate restTemplate, PortfolioRepository portfolioRepository) {
        this.restTemplate = restTemplate;
        this.portfolioRepository = portfolioRepository;
    }

    public Stock fetchStockDetails(Stock stock) {
        String url = buildAlphaVantageUrl(stock.getStockSymbol());
        logger.info("Fetching stock details from URL: {}", url);
        AlphaVantageResponse response = restTemplate.getForObject(url, AlphaVantageResponse.class);

        AlphaVantageResponse.GlobalQuote quote = Optional.ofNullable(response)
                .map(AlphaVantageResponse::getGlobalQuote)
                .orElseThrow(() -> new FetchStockDetailsException("Failed to fetch stock details for: " + stock.getStockSymbol()));

        stock.setPurchasePrice(quote.getPrice());
        logger.info("Fetched stock details for {}: {}", stock.getStockSymbol(), quote);
        return stock;
    }

    private String buildAlphaVantageUrl(String stockSymbol) {
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .queryParam("function", "GLOBAL_QUOTE")
                .queryParam("symbol", stockSymbol)
                .queryParam("apikey", apiKey)
                .toUriString();
    }

    public Optional<Stock> stockExists(Stock stock) {
        return portfolioRepository.findById(stock.getStockSymbol());
    }

    public void addStock(Stock stock) {
        portfolioRepository.save(stock);
    }

    public void deleteStock(String stockName) {
        Optional<Stock> stock = portfolioRepository.findById(stockName);
        stock.ifPresent(portfolioRepository::delete);
    }

    public List<Stock> getStocks() {
        return portfolioRepository.findAll();
    }
}
