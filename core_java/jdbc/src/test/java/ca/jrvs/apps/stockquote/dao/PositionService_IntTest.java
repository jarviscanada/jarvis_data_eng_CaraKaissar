package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.models.Position;
import ca.jrvs.apps.stockquote.dao.services.PositionService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.sql.SQLException;
import java.util.Optional;

public class PositionService_IntTest {

    private PositionService positionService;
    private PositionDao positionDao;

    @Before
    public void setUp() throws SQLException {
        // Initialize the real DAO and service
        positionDao = new PositionDao(); // Ensure this connects to the correct PostgreSQL instance
        positionService = new PositionService(positionDao);

        // Clean up the database before each test
        positionDao.deleteAll(); // Make sure the database is in a clean state
    }

    @Test
    public void testBuy_NewPosition_IntTest() {
        String ticker = "AAPL";
        int numberOfShares = 100;
        double price = 150.00;

        // Call the service method to buy shares
        Position result = positionService.buy(ticker, numberOfShares, price);

        // Verify the result
        assertNotNull(result);
        assertEquals(ticker, result.getTicker());
        assertEquals(numberOfShares, result.getNumOfShares());
        assertEquals(numberOfShares * price, result.getValuePaid(), 0.01);

        // Verify the data in the database
        Optional<Position> positionFromDb = positionDao.findById(ticker);
        assertTrue(positionFromDb.isPresent());
        assertEquals(result.getTicker(), positionFromDb.get().getTicker());
    }

    @Test
    public void testBuy_ExistingPosition_IntTest() {
        String ticker = "AAPL";
        int numberOfShares = 50;
        double price = 150.00;

        // Create a position directly in the database
        Position existingPosition = new Position();
        existingPosition.setTicker(ticker);
        existingPosition.setNumOfShares(100);
        existingPosition.setValuePaid(15000.00); // 100 shares at $150.00
        positionDao.save(existingPosition);

        // Call the service method to buy more shares
        Position result = positionService.buy(ticker, numberOfShares, price);

        // Verify the result
        assertNotNull(result);
        assertEquals(150, result.getNumOfShares()); // 100 existing + 50 new
        assertEquals(15000.00 + (50 * 150.00), result.getValuePaid(), 0.01);

        // Verify the data in the database
        Optional<Position> positionFromDb = positionDao.findById(ticker);
        assertTrue(positionFromDb.isPresent());
        assertEquals(result.getNumOfShares(), positionFromDb.get().getNumOfShares());
    }

    @Test
    public void testSell_ExistingPosition_IntTest() {
        String ticker = "AAPL";

        Position existingPosition = new Position();
        existingPosition.setTicker(ticker);
        existingPosition.setNumOfShares(100);
        existingPosition.setValuePaid(15000.00);
        positionDao.save(existingPosition);

        positionService.sell(ticker);

        Optional<Position> positionFromDb = positionDao.findById(ticker);
        assertFalse(positionFromDb.isPresent());
    }
}

