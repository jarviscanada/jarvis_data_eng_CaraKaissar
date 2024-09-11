package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.models.Quote;
import ca.jrvs.apps.stockquote.dao.models.Position;
import org.junit.*;
import java.sql.*;
import java.util.Optional;

import static org.junit.Assert.*;

public class PositionDao_Test {

    private static Connection connection;
    private static PositionDao positionDao;
    private static QuoteDao quoteDao; // Adding QuoteDao to manage Quote records

    @BeforeClass
    public static void setUpClass() throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost:5432/stock_quote";
        String dbUser = "postgres";
        String dbPassword = "newpassword";
        DatabaseConnection dbConnection = new DatabaseConnection(dbUrl, dbUser, dbPassword); // Initialize using constructor

        connection = dbConnection.getConnection();
        positionDao = new PositionDao(connection);
        quoteDao = new QuoteDao(connection); // Initialize QuoteDao for Quote operations
    }

    @Before
    public void setUp() throws SQLException {
        System.out.println("Running setup: clearing the position and quote tables");

        // Verify the connection is still valid before running the cleanup
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not initialized or is closed.");
        }

        // Clean up the database before each test
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM position");
            stmt.executeUpdate("DELETE FROM quote");
        } catch (SQLException e) {
            System.err.println("Error during setup: Unable to clear the 'position' or 'quote' tables. " + e.getMessage());
            throw e;
        }
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void createRequiredQuote(String symbol) {
        Quote quote = new Quote();
        quote.setTicker(symbol);
        quote.setOpen(150.00);
        quote.setHigh(155.00);
        quote.setLow(148.00);
        quote.setPrice(152.00);
        quote.setVolume(10000);
        quote.setLatestTradingDay(Date.valueOf("2024-08-28"));
        quote.setPreviousClose(151.00);
        quote.setChange(1.00);
        quote.setChangePercent("0.66%");
        quote.setTimestamp(new Timestamp(System.currentTimeMillis()));

        quoteDao.save(quote);
    }

    @Test
    public void testSave() {
        // Create the required Quote first
        createRequiredQuote("AAPL");

        // Create a Position object
        Position position = new Position();
        position.setTicker("AAPL");
        position.setNumOfShares(100);
        position.setValuePaid(15000.00);

        // Test saving the position
        positionDao.save(position);

        // Verify that the position was saved correctly
        Optional<Position> fetchedPosition = positionDao.findById("AAPL");
        assertTrue(fetchedPosition.isPresent());
        assertEquals("AAPL", fetchedPosition.get().getTicker());
        assertEquals(100, fetchedPosition.get().getNumOfShares());
        assertEquals(15000.00, fetchedPosition.get().getValuePaid(), 0.01);
    }

    @Test
    public void testFindById() {
        // Create the required Quote first
        createRequiredQuote("AAPL");

        // Setup initial data
        Position position = new Position();
        position.setTicker("AAPL");
        position.setNumOfShares(100);
        position.setValuePaid(15000.00);

        // Save the position to the database
        positionDao.save(position);

        // Test findById with a valid ID
        Optional<Position> fetchedPosition = positionDao.findById("AAPL");
        assertTrue(fetchedPosition.isPresent());
        assertEquals("AAPL", fetchedPosition.get().getTicker());

        // Test findById with an invalid ID
        Optional<Position> missingPosition = positionDao.findById("MSFT");
        assertFalse(missingPosition.isPresent());
    }

    @Test
    public void testFindAll() {
        // Create the required Quotes first
        createRequiredQuote("AAPL");
        createRequiredQuote("MSFT");

        // Setup initial data
        Position position1 = new Position();
        position1.setTicker("AAPL");
        position1.setNumOfShares(100);
        position1.setValuePaid(15000.00);

        Position position2 = new Position();
        position2.setTicker("MSFT");
        position2.setNumOfShares(200);
        position2.setValuePaid(30000.00);

        // Save both positions
        positionDao.save(position1);
        positionDao.save(position2);

        // Test findAll
        Iterable<Position> positions = positionDao.findAll();
        int count = 0;
        for (Position p : positions) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testDeleteById() {
        // Create the required Quote first
        createRequiredQuote("AAPL");

        // Setup initial data
        Position position = new Position();
        position.setTicker("AAPL");
        position.setNumOfShares(100);
        position.setValuePaid(15000.00);

        // Save the position to the database
        positionDao.save(position);

        // Test deleteById
        positionDao.deleteById("AAPL");
        Optional<Position> fetchedPosition = positionDao.findById("AAPL");
        assertFalse(fetchedPosition.isPresent());
    }

    @Test
    public void testDeleteAll() {
        // Create the required Quotes first
        createRequiredQuote("AAPL");
        createRequiredQuote("MSFT");

        // Setup initial data
        Position position1 = new Position();
        position1.setTicker("AAPL");
        position1.setNumOfShares(100);
        position1.setValuePaid(15000.00);

        Position position2 = new Position();
        position2.setTicker("MSFT");
        position2.setNumOfShares(200);
        position2.setValuePaid(30000.00);

        // Save both positions
        positionDao.save(position1);
        positionDao.save(position2);

        // Test deleteAll
        positionDao.deleteAll();
        Iterable<Position> positions = positionDao.findAll();
        int count = 0;
        for (Position p : positions) {
            count++;
        }
        assertEquals(0, count);
    }
}
