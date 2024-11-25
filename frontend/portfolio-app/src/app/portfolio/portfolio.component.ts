import { Component, OnInit } from '@angular/core';
import { StockService } from '../services/stock.service';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-portfolio',
  templateUrl: './portfolio.component.html',
  styleUrls: ['./portfolio.component.css']
})

export class PortfolioComponent implements OnInit {
  stockSymbol: string = '';
  stockQuantity: number = 1;
  quantities: number[] = [1, 2, 3, 4, 5];
  portfolio: any[] = [];
  totalValue: number = 0;
  message: string = '';

constructor(private stockService: StockService) {}

  ngOnInit() {
    this.refreshPortfolio();
  }

  addStock() {
    const stock = {
      stockSymbol: this.stockSymbol,
      stockQuantity: this.stockQuantity
    };

    this.stockService.addStock(stock).subscribe(
      (stock: any) => {
        this.message = `${stock.stockQuantity} stock(s) of ${stock.stockSymbol} added successfully at price $${stock.purchasePrice}!`;
        this.refreshPortfolio(); // Refresh the portfolio list
      },
      (error) => {
        // Handle error response from the backend
        if (error.status === 400) {
          this.message = `Invalid stock symbol: ${this.stockSymbol}`;
        } else {
          this.message = `An error occurred: ${error.message}`;
        }
      }
    );
  }

  deleteStock() {
    this.stockService.deleteStock(this.stockSymbol).subscribe(() => {
      this.message = `Stock ${this.stockSymbol} removed if it existed.`;
      this.refreshPortfolio();
    });
  }

  refreshPortfolio() {
    this.stockService.getPortfolio().subscribe((data) => {
      this.portfolio = data;
      this.calculateTotalValue();
    });
  }

  calculateTotalValue() {
    this.totalValue = this.portfolio.reduce((sum, stock) => sum + stock.stockQuantity * stock.purchasePrice, 0);
  }
}
