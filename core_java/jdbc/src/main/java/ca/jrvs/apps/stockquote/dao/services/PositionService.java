package ca.jrvs.apps.stockquote.dao.services;

import ca.jrvs.apps.stockquote.dao.models.Position;
import ca.jrvs.apps.stockquote.dao.PositionDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.Optional;

public class PositionService {

    private static final Logger logger = LoggerFactory.getLogger(PositionService.class);
    private PositionDao dao;

    // Constructor to inject PositionDao
    public PositionService(PositionDao dao) {
        this.dao = dao;
    }

    /**
     * Processes a buy order and updates the database accordingly.
     * If the position already exists, update the number of shares and the value paid.
     * If not, create a new position.
     * @param ticker - Stock ticker symbol.
     * @param numberOfShares - Number of shares to buy.
     * @param price - Price per share.
     * @return The updated position in the database after processing the buy.
     */
    public Position buy(String ticker, int numberOfShares, double price) {
        logger.info("Processing buy order for ticker: {}, shares: {}, price: {}", ticker, numberOfShares, price);

        if (ticker == null || ticker.isEmpty() || numberOfShares <= 0 || price <= 0) {
            logger.error("Invalid input for buying position: ticker={}, shares={}, price={}", ticker, numberOfShares, price);
            throw new IllegalArgumentException("Invalid input for buying position.");
        }

        Optional<Position> existingPositionOpt = dao.findById(ticker);
        Position position;
        if (existingPositionOpt.isPresent()) {
            // Update existing position
            logger.info("Updating existing position for ticker: {}", ticker);
            position = existingPositionOpt.get();
            int totalShares = position.getNumOfShares() + numberOfShares;
            double totalValue = position.getValuePaid() + (numberOfShares * price);
            position.setNumOfShares(totalShares);
            position.setValuePaid(totalValue);
        } else {
            // Create new position
            logger.info("Creating new position for ticker: {}", ticker);
            position = new Position();
            position.setTicker(ticker);
            position.setNumOfShares(numberOfShares);
            position.setValuePaid(numberOfShares * price);
        }

        dao.save(position);
        logger.info("Position saved for ticker: {}", ticker);
        return position;
    }

    /**
     * Sells all shares of the given ticker symbol by deleting the position.
     * @param ticker - Stock ticker symbol.
     */
    public void sell(String ticker) {
        logger.info("Processing sell order for ticker: {}", ticker);
        if (ticker == null || ticker.isEmpty()) {
            logger.error("Invalid input: ticker is null or empty.");
            throw new IllegalArgumentException("Invalid input: ticker is null or empty.");
        }

        // Delete the position from the database if it exists
        if (dao.findById(ticker).isPresent()) {
            logger.info("Position deleted for ticker: {}", ticker);
            dao.deleteById(ticker);
        } else {
            logger.error("No position found for ticker: {}", ticker);
            throw new IllegalArgumentException("No position found for ticker: " + ticker);
        }
    }

    /**
     * Find position by its ticker symbol.
     * @param ticker - Stock ticker symbol.
     * @return The position if found, or an empty Optional if not.
     */
    public Optional<Position> findPositionById(String ticker) {
        if (ticker == null || ticker.isEmpty()) {
            logger.error("Invalid input: ticker is null or empty.");
            throw new IllegalArgumentException("Invalid input: ticker is null or empty.");
        }
        logger.info("Finding position for ticker: {}", ticker);
        return dao.findById(ticker);
    }
}