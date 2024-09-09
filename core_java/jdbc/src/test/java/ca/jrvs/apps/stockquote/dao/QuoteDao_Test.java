package ca.jrvs.apps.stockquote.dao;
import ca.jrvs.apps.stockquote.dao.models.Quote;
import org.junit.*;
import java.sql.*;
import java.util.Optional;

import static org.junit.Assert.*;

public class QuoteDao_Test {

    private static Connection connection;
    private static QuoteDao quoteDao;

    @BeforeClass
    public static void setUpClass() throws SQLException {
        // Initialize connection using DatabaseConnection
        connection = DatabaseConnection.getConnection();
        quoteDao = new QuoteDao();
    }

    @AfterClass
    public static void tearDownClass() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Before
    public void setUp() throws SQLException {
        // Verify the connection is still valid before running the cleanup
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is not initialized or is closed.");
        }

        // Clean up the database before each test
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DELETE FROM position");
            stmt.executeUpdate("DELETE FROM quote");
        } catch (SQLException e) {
            System.err.println("Error during setup: Unable to clear the 'quote' or 'position' table. " + e.getMessage());
            throw e;
        }
    }


    @Test
    public void testSave() {
        Quote quote = new Quote();
        quote.setTicker("AAPL");
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

        // Test save
        quoteDao.save(quote);

        // Verify that the quote was saved correctly
        Optional<Quote> fetchedQuote = quoteDao.findById("AAPL");
        assertTrue(fetchedQuote.isPresent());
        assertEquals("AAPL", fetchedQuote.get().getTicker());
        assertEquals(150.00, fetchedQuote.get().getOpen(), 0.01);
        assertEquals(155.00, fetchedQuote.get().getHigh(), 0.01);
        assertEquals(148.00, fetchedQuote.get().getLow(), 0.01);
        assertEquals(152.00, fetchedQuote.get().getPrice(), 0.01);
        assertEquals(10000, fetchedQuote.get().getVolume());
        assertEquals(Date.valueOf("2024-08-28"), fetchedQuote.get().getLatestTradingDay());
        assertEquals(151.00, fetchedQuote.get().getPreviousClose(), 0.01);
        assertEquals(1.00, fetchedQuote.get().getChange(), 0.01);
        assertEquals("0.66%", fetchedQuote.get().getChangePercent());
        assertNotNull(fetchedQuote.get().getTimestamp());
    }

    @Test
    public void testFindById() {
        // Setup initial data
        Quote quote = new Quote();
        quote.setTicker("AAPL");
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

        // Save the quote to the database
        quoteDao.save(quote);

        // Test findById with a valid ID
        Optional<Quote> fetchedQuote = quoteDao.findById("AAPL");
        assertTrue(fetchedQuote.isPresent());
        assertEquals("AAPL", fetchedQuote.get().getTicker());

        // Test findById with an invalid ID
        Optional<Quote> missingQuote = quoteDao.findById("MSFT");
        assertFalse(missingQuote.isPresent());
    }

    @Test
    public void testFindAll() {
        // Setup initial data
        Quote quote1 = new Quote();
        quote1.setTicker("AAPL");
        quote1.setOpen(150.00);
        quote1.setHigh(155.00);
        quote1.setLow(148.00);
        quote1.setPrice(152.00);
        quote1.setVolume(10000);
        quote1.setLatestTradingDay(Date.valueOf("2024-08-28"));
        quote1.setPreviousClose(151.00);
        quote1.setChange(1.00);
        quote1.setChangePercent("0.66%");
        quote1.setTimestamp(new Timestamp(System.currentTimeMillis()));

        Quote quote2 = new Quote();
        quote2.setTicker("MSFT");
        quote2.setOpen(250.00);
        quote2.setHigh(255.00);
        quote2.setLow(248.00);
        quote2.setPrice(252.00);
        quote2.setVolume(20000);
        quote2.setLatestTradingDay(Date.valueOf("2024-08-28"));
        quote2.setPreviousClose(251.00);
        quote2.setChange(1.00);
        quote2.setChangePercent("0.40%");
        quote2.setTimestamp(new Timestamp(System.currentTimeMillis()));

        // Save both quotes
        quoteDao.save(quote1);
        quoteDao.save(quote2);

        // Test findAll
        Iterable<Quote> quotes = quoteDao.findAll();
        int count = 0;
        for (Quote q : quotes) {
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testDeleteById() {
        // Setup initial data
        Quote quote = new Quote();
        quote.setTicker("AAPL");
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

        // Save the quote to the database
        quoteDao.save(quote);

        // Test deleteById
        quoteDao.deleteById("AAPL");
        Optional<Quote> fetchedQuote = quoteDao.findById("AAPL");
        assertFalse(fetchedQuote.isPresent());
    }

    @Test
    public void testDeleteAll() throws SQLException {
        // Setup initial data
        Quote quote1 = new Quote();
        quote1.setTicker("AAPL");
        quote1.setOpen(150.00);
        quote1.setHigh(155.00);
        quote1.setLow(148.00);
        quote1.setPrice(152.00);
        quote1.setVolume(10000);
        quote1.setLatestTradingDay(Date.valueOf("2024-08-28"));
        quote1.setPreviousClose(151.00);
        quote1.setChange(1.00);
        quote1.setChangePercent("0.66%");
        quote1.setTimestamp(new Timestamp(System.currentTimeMillis()));

        Quote quote2 = new Quote();
        quote2.setTicker("MSFT");
        quote2.setOpen(250.00);
        quote2.setHigh(255.00);
        quote2.setLow(248.00);
        quote2.setPrice(252.00);
        quote2.setVolume(20000);
        quote2.setLatestTradingDay(Date.valueOf("2024-08-28"));
        quote2.setPreviousClose(251.00);
        quote2.setChange(1.00);
        quote2.setChangePercent("0.40%");
        quote2.setTimestamp(new Timestamp(System.currentTimeMillis()));

        // Save both quotes
        quoteDao.save(quote1);
        quoteDao.save(quote2);

        // Test deleteAll
        quoteDao.deleteAll();
        Iterable<Quote> quotes = quoteDao.findAll();
        int count = 0;
        for (Quote q : quotes) {
            count++;
        }
        assertEquals(0, count);
    }

}