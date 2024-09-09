package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.models.Quote;
import ca.jrvs.apps.stockquote.dao.services.QuoteService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Optional;


public class QuoteService_UnitTest {

    private QuoteService quoteService;

    @Mock
    private QuoteDao mockDao;

    @Mock
    private QuoteHttpHelper mockHttpHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        quoteService = new QuoteService(mockDao,mockHttpHelper);


    }

    @Test
    public void testFetchQuoteDataFromAPI_ValidTicker() {
        String ticker = "AAPL";
        Quote mockQuote = new Quote();
        mockQuote.setTicker(ticker);

        // Configure mocks
        when(mockHttpHelper.fetchQuoteInfo(ticker)).thenReturn(mockQuote);

        // Call the method to test
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(ticker);

        // Verify the result
        assertTrue(result.isPresent());
        assertEquals(ticker, result.get().getTicker());

        // Verify interactions
        verify(mockHttpHelper).fetchQuoteInfo(ticker);
        verify(mockDao).save(mockQuote);
    }

    @Test (expected = IllegalArgumentException.class)
    public void testFetchQuoteDataFromAPI_InvalidTicker() {
        String ticker = "INVALID";

        // Configure mocks to return null or throw an exception
        when(mockHttpHelper.fetchQuoteInfo(ticker)).thenReturn(null);

        // Call the method to test
        Optional<Quote> result = quoteService.fetchQuoteDataFromAPI(ticker);

        // Verify the result is empty
        assertFalse(result.isPresent());

        // Verify interactions
        verify(mockHttpHelper).fetchQuoteInfo(ticker);
        verify(mockDao, never()).save(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchQuoteDataFromAPI_NullTicker() {
        quoteService.fetchQuoteDataFromAPI(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchQuoteDataFromAPI_EmptyTicker() {
        quoteService.fetchQuoteDataFromAPI("");
    }
}

