package ca.jrvs.apps.stockquote.dao.controller;

import ca.jrvs.apps.stockquote.dao.models.Position;
import ca.jrvs.apps.stockquote.dao.services.PositionService;
import ca.jrvs.apps.stockquote.dao.services.QuoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class StockQuoteController {

    private static final Logger logger = LoggerFactory.getLogger(StockQuoteController.class);


    private QuoteService quoteService;
    private PositionService positionService;
    private Scanner scanner;

    // Constructor to initialize services
    public StockQuoteController(QuoteService quoteService, PositionService positionService) {
        this.quoteService = quoteService;
        this.positionService = positionService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * User interface for the application.
     * Loops indefinitely to allow the user to interact with the app.
     */
    public void initClient() {
        logger.info("Starting Stock Quote and Position Manager");
        System.out.println("Welcome to the Stock Quote and Position Manager!");
        boolean running = true;

        // Main loop for the client interface
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim().toLowerCase();
            logger.info("Choice is {}", choice);
            switch (choice) {
                case "1":
                    fetchQuoteInfo();
                    break;
                case "2":
                    buyStock();
                    break;
                case "3":
                    sellStock();
                    break;
                case "4":
                    viewPosition();
                    break;
                case "q":
                    running = false;
                    logger.info("Stopping Stock Quote and Position Manager");
                    break;
                default:
                    System.out.println("Invalid input. Please choose an option from the menu.");
            }
        }
        logger.info("Application stopped.");

        System.out.println("Goodbye!");
    }

    // Print the menu options for the user
    private void printMenu() {
        System.out.println("\nPlease choose an option:");
        System.out.println("1. Get stock quote information");
        System.out.println("2. Buy a stock");
        System.out.println("3. Sell a stock");
        System.out.println("4. View current position");
        System.out.println("q. Quit");
        System.out.print("Enter choice: ");
    }

    // Fetch stock quote information
    private void fetchQuoteInfo() {
        logger.info("Fetching stock quote information.");
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        try {
            quoteService.fetchQuoteDataFromAPI(symbol).ifPresentOrElse(
                    quote -> {
                        logger.info("Successfully fetched stock quote for symbol: {}", symbol);
                        System.out.println("Stock Information for " + symbol + ":");
                        System.out.println("Price: " + quote.getPrice());
                        System.out.println("Volume: " + quote.getVolume());
                        System.out.println("Open: " + quote.getOpen());
                        System.out.println("High: " + quote.getHigh());
                        System.out.println("Low: " + quote.getLow());
                    },
                    () -> {
                        logger.warn("No data found for symbol: {}", symbol);
                        System.out.println("Could not retrieve data for symbol: " + symbol);
                    }
            );
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching quote for symbol: {}. Message: {}", symbol, e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }


    private void buyStock() {
        logger.info("Processing stock purchase.");
        System.out.print("Enter stock symbol to buy: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        try {
            if (!quoteService.quoteExists(symbol)) {
                logger.info("Fetching and saving quote for symbol: {}", symbol);
                quoteService.fetchQuoteDataFromAPI(symbol).ifPresentOrElse(
                        quote -> {
                            try {
                                quoteService.saveQuote(quote);
                                logger.info("Quote saved for symbol: {}", symbol);
                            } catch (Exception e) {
                                logger.error("Error saving quote for symbol: {}", symbol, e);
                                System.out.println("Error saving the quote: " + e.getMessage());
                            }
                        },
                        () -> logger.warn("No data found for symbol: {}", symbol)
                );
            }

            System.out.print("Enter the number of shares: ");
            int shares = Integer.parseInt(scanner.nextLine().trim());
            System.out.print("Enter the price per share: ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            Position updatedPosition = positionService.buy(symbol, shares, price);
            logger.info("Position updated for symbol: {}", symbol);
            System.out.println("Updated Position for " + symbol + ":");
            System.out.println("Total Shares: " + updatedPosition.getNumOfShares());
            System.out.println("Total Value Paid: $" + updatedPosition.getValuePaid());
        } catch (NumberFormatException e) {
            logger.error("Invalid input for shares or price.", e);
            System.out.println("Invalid input. Please enter valid numbers for shares and price.");
        } catch (IllegalArgumentException e) {
            logger.error("Error buying stock for symbol: {}. Message: {}", symbol, e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }


    /**
     * Handle selling all shares of a given stock by prompting the user for the stock symbol.
     * Deletes the stock position from the database.
     */
    private void sellStock() {
        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        try {
            positionService.sell(symbol);
            System.out.println("Sold all shares of " + symbol + ".");
        } catch (IllegalArgumentException e) {
            logger.error("Error sellin stock for symbol: {}. Message: {}", symbol, e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * View the current stock position by prompting the user for the stock symbol.
     * Displays the number of shares and total value paid.
     */
    private void viewPosition() {
        System.out.print("Enter stock symbol to view position: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        positionService.findPositionById(symbol).ifPresentOrElse(
                position -> {
                    System.out.println("Position for " + symbol + ":");
                    System.out.println("Shares owned: " + position.getNumOfShares());
                    System.out.println("Total value paid: $" + position.getValuePaid());
                },
                () -> System.out.println("No position found for symbol: " + symbol)
        );
    }
}

