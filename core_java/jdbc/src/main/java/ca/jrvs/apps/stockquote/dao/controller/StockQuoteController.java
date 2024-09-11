package ca.jrvs.apps.stockquote.dao.controller;

import ca.jrvs.apps.stockquote.dao.models.Position;
import ca.jrvs.apps.stockquote.dao.services.PositionService;
import ca.jrvs.apps.stockquote.dao.services.QuoteService;
import java.util.Scanner;

public class StockQuoteController {

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
        System.out.println("Welcome to the Stock Quote and Position Manager!");
        boolean running = true;

        // Main loop for the client interface
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim().toLowerCase();

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
                    break;
                default:
                    System.out.println("Invalid input. Please choose an option from the menu.");
            }
        }

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
        System.out.print("Enter stock symbol: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        try {
            quoteService.fetchQuoteDataFromAPI(symbol).ifPresentOrElse(
                    quote -> {
                        System.out.println("Stock Information for " + symbol + ":");
                        System.out.println("Price: " + quote.getPrice());
                        System.out.println("Open: " + quote.getOpen());
                        System.out.println("High: " + quote.getHigh());
                        System.out.println("Low: " + quote.getLow());
                        System.out.println("Volume: " + quote.getVolume());
                    },
                    () -> System.out.println("Could not retrieve data for symbol: " + symbol)
            );
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Handle buying a stock
    private void buyStock() {
        System.out.print("Enter stock symbol to buy: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        try {
            // Check if the quote already exists in the quote table
            if (!quoteService.quoteExists(symbol)) {
                // Fetch the stock quote and save it if it's not already present in the quote table
                quoteService.fetchQuoteDataFromAPI(symbol).ifPresentOrElse(
                        quote -> {
                            try {
                                quoteService.saveQuote(quote);  // Save the quote to the quote table
                            } catch (Exception e) {
                                System.out.println("Error saving the quote: " + e.getMessage());
                            }
                        },
                        () -> System.out.println("Could not retrieve data for symbol: " + symbol)
                );
            }

            // Ask for the number of shares and price
            System.out.print("Enter the number of shares: ");
            int shares = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter the price per share: ");
            double price = Double.parseDouble(scanner.nextLine().trim());

            // Process the buy order
            Position updatedPosition = positionService.buy(symbol, shares, price);
            System.out.println("Updated Position for " + symbol + ":");
            System.out.println("Total Shares: " + updatedPosition.getNumOfShares());
            System.out.println("Total Value Paid: $" + updatedPosition.getValuePaid());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers for shares and price.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }



    // Handle selling a stock
    private void sellStock() {
        System.out.print("Enter stock symbol to sell: ");
        String symbol = scanner.nextLine().trim().toUpperCase();

        try {
            positionService.sell(symbol);
            System.out.println("Sold all shares of " + symbol + ".");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // View current stock position
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

