package com.jpm.stockmanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Stock {

    @Id
    private String stockSymbol;
    private int stockQuantity;
    private double purchasePrice;
}
