import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})

export class StockService {
  private backendApiUrl = 'http://localhost:8080/api/portfolio';

  constructor(private http: HttpClient) {}

  addStock(stock: any): Observable<any> {
    return this.http.post(`${this.backendApiUrl}/add-stock`, stock);
  }

  deleteStock(stockSymbol: string): Observable<any> {
    return this.http.delete(`${this.backendApiUrl}/delete-stock/${stockSymbol}`, { responseType: 'text' });
  }

  getPortfolio(): Observable<any[]> {
    return this.http.get<any[]>(`${this.backendApiUrl}/stocks`);
  }
}
