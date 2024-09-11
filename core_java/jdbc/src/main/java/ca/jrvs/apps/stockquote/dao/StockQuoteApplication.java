package ca.jrvs.apps.stockquote.dao;
import ca.jrvs.apps.stockquote.dao.controller.StockQuoteController;
import ca.jrvs.apps.stockquote.dao.services.PositionService;
import ca.jrvs.apps.stockquote.dao.services.QuoteService;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class StockQuoteApplication {

    public static void main(String[] args) {
        try {
            // Step 1: Parse properties.txt directly in the main method
            Properties properties = new Properties();
            try (FileInputStream fis = new FileInputStream("core_java/jdbc/src/resources/properties.txt")) {
                properties.load(fis);
            }

            // Load database and API configurations from the properties file
            String dbUrl = "jdbc:postgresql://" + properties.getProperty("server") + ":" + properties.getProperty("port") + "/" + properties.getProperty("database");
            String dbUser = properties.getProperty("username");
            String dbPassword = properties.getProperty("password");
            String apiKey = properties.getProperty("api-key");

            // Step 2: Instantiate the DatabaseConnection using constructor
            DatabaseConnection dbConnection = new DatabaseConnection(dbUrl, dbUser, dbPassword);
            Connection connection = dbConnection.getConnection();

            // Create the DAO (QuoteDao) and the service (QuoteService)
            QuoteDao quoteDao = new QuoteDao(connection);
            PositionDao positionDao = new PositionDao(connection);
            QuoteHttpHelper httpHelper = new QuoteHttpHelper(apiKey);
            QuoteService quoteService = new QuoteService(quoteDao, httpHelper);
            PositionService positionService = new PositionService(positionDao);

            // Step 3: Start the user interface (StockQuoteController)
            StockQuoteController controller = new StockQuoteController(quoteService, positionService); // Pass both
            controller.initClient();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }
}
