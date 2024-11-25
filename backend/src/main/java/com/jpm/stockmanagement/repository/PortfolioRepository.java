package com.jpm.stockmanagement.repository;

import com.jpm.stockmanagement.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Stock, String> {
}
