package com.jpm.stockmanagement.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AlphaVantageResponse {

    @JsonProperty("Global Quote")
    private GlobalQuote globalQuote;

    @Data
    public static class GlobalQuote {
        @JsonProperty("01. symbol")
        private String symbol;
        @JsonProperty("05. price")
        private double price;
    }
}

