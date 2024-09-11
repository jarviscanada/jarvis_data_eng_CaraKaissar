package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.models.Quote;
import ca.jrvs.apps.stockquote.dao.services.QuoteService;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class QuoteService_IntTest {

    private QuoteService quoteService;
    private QuoteDao quoteDao;
    private QuoteHttpHelper mockHttpHelper;

    @Before
    public void setUp() throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost:5432/stock_quote";
        String dbUser = "postgres";
        String dbPassword = "newpassword";
        DatabaseConnection dbConnec = new DatabaseConnection(dbUrl, dbUser, dbPassword);
        Connection connection = dbConnec.getConnection();
        quoteDao = new QuoteDao(connection);
        PositionDao positionDao = new PositionDao(connection);

        positionDao.deleteAll();
        quoteDao.deleteAll();

        mockHttpHelper = mock(QuoteHttpHelper.class);
        quoteService = new QuoteService(quoteDao, mockHttpHelper);
    }


    @Test
    public void testFetchQuoteDataFromAPI_Integration() {
        String ticker = "AAPL";

        // Create a mock Quote object to simulate the API response
        Quote mockQuote = new Quote();
        mockQuote.setTicker(ticker);
        mockQuote.setOpen(150.00);
        mockQuote.setHigh(155.00);
        mockQuote.setLow(148.00);
        mockQuote.setPrice(152.00);
        mockQuote.setVolume(10000);
        mockQuote.setChange(1.00);
        mockQuote.setChangePercent("0.66%");
        mockQuote.setLatestTradingDay(new java.util.Date()); // Set a valid date here

        when(mockHttpHelper.fetchQuoteInfo(ticker)).thenReturn(mockQuote);

        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(ticker);

        assertTrue(result.isPresent());
        assertEquals(ticker, result.get().getTicker());

        Optional<Quote> quoteFromDb = quoteDao.findById(ticker);
        assertTrue(quoteFromDb.isPresent());
        assertEquals(ticker, quoteFromDb.get().getTicker());
        assertEquals(150.00, quoteFromDb.get().getOpen(), 0.01);
    }

}
