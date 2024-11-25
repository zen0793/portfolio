# Stock Portfolio Management Application

## Project Overview
This is a stock portfolio management application that enables users to:
- Add stocks to their portfolio
- Remove stocks from their portfolio.
- View a list of stocks along with their total portfolio value.

The application includes:
- A **backend** built with Spring Boot
- A **frontend** built with Angular
- Unit and integration tests for the backend
- A h2 database depicting the portfolio

---

## Features
1. Add stocks by providing a stock symbol and quantity.
2. Remove stocks from the portfolio.
3. Fetch real-time stock prices from the Alpha Vantage API.
4. Calculate the total portfolio value based on current stock prices.

---

## Setup Instructions

### Backend
1. **Prerequisites**:
    - Java 17+
    - An IDE like IntelliJ
    - Gradle
2. **Steps**:
    - Navigate to the backend folder:
    - Identify the build.gradle file, right-click and link the gradle file
    - Once linked, you can build the project and run the springboot application 
   by right-clicking the PortfolioApplication file and clicking on run. This will launch the springboot application which will be available at http://localhost:8080
    - Alternatively, you can go to the terminal and in /backend, you can run `./gradlew bootRun`
3. **Database**:
    - The H2 Console will be accessible at http://localhost:8080/h2-console
    - Default credentials are in `application.properties` which is located within src/main/resources

### Frontend
1. **Prerequisites**:
    - Node.js
    - Angular CLI
2. **Steps**:
    - Navigate to the frontend folder:
    - Install dependencies by:
      ```bash
      npm install
      ```
    - Start the Angular development server:
      ```bash
      ng serve
      ```
    - The frontend will be available at http://localhost:4200

---

You can now add a stock such as AAPL in the field. The web app will send a request to the microservice with the symbol, 
where it will populate the real-time stock price, and add it to the database. You will see the stock being displayed on the
webapp as well.

## API Spec

The API documentation can be accessed by opening this link in the browser:
http://localhost:8080/swagger-ui/index.html