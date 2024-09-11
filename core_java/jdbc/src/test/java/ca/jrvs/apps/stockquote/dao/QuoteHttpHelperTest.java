package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.models.Quote;
import okhttp3.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class QuoteHttpHelperTest {

    @Mock
    private OkHttpClient mockClient;

    @Mock
    private Call mockCall;

    @Mock
    private Response mockResponse;

    @Mock
    private ResponseBody mockResponseBody;

    @InjectMocks
    private QuoteHttpHelper quoteHttpHelper;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        quoteHttpHelper = new QuoteHttpHelper("70e344c139msh8b96a0b085f24bfp1ae4c4jsn8a14887b51c5");
        quoteHttpHelper.client = mockClient;
    }

    @Test
    public void testFetchQuoteInfoSuccess() throws IOException {
        String symbol = "AAPL";
        String mockJsonResponse = "{ \"Global Quote\": { " +
                "\"01. symbol\": \"AAPL\", " +
                "\"02. open\": \"145.00\", " +
                "\"03. high\": \"150.00\", " +
                "\"04. low\": \"144.00\", " +
                "\"05. price\": \"148.00\", " +
                "\"06. volume\": \"500000\", " +
                "\"07. latest trading day\": \"2024-08-27\", " +
                "\"08. previous close\": \"147.00\", " +
                "\"09. change\": \"1.00\", " +
                "\"10. change percent\": \"0.68%\" } }";

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(mockJsonResponse);

        Quote result = quoteHttpHelper.fetchQuoteInfo(symbol);

        assertNotNull(result);
        assertEquals("AAPL", result.getSymbol());
        assertEquals(145.00, result.getOpen(), 0.01);
        assertEquals(150.00, result.getHigh(), 0.01);
        assertEquals(144.00, result.getLow(), 0.01);
        assertEquals(148.00, result.getPrice(), 0.01);
        assertEquals(500000, result.getVolume());

        // Use UTC timezone for date formatting
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Force UTC timezone
        String formattedDate = dateFormat.format(result.getLatestTradingDay());
        assertEquals("2024-08-27", formattedDate);  // Compare formatted date string

        assertEquals(147.00, result.getPreviousClose(), 0.01);
        assertEquals(1.00, result.getChange(), 0.01);
        assertEquals("0.68%", result.getChangePercent());
    }



    @Test(expected = IllegalArgumentException.class)
    public void testFetchQuoteInfoInvalidSymbol() {
        String symbol = "";

        quoteHttpHelper.fetchQuoteInfo(symbol);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchQuoteInfoNoData() throws IOException {
        String symbol = "INVALID";
        String mockJsonResponse = "{}"; 

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenReturn(mockResponse);
        when(mockResponse.isSuccessful()).thenReturn(true);
        when(mockResponse.body()).thenReturn(mockResponseBody);
        when(mockResponseBody.string()).thenReturn(mockJsonResponse);

        quoteHttpHelper.fetchQuoteInfo(symbol);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFetchQuoteInfoIOException() throws IOException {
        String symbol = "AAPL";

        when(mockClient.newCall(any(Request.class))).thenReturn(mockCall);
        when(mockCall.execute()).thenThrow(new IOException("Network error"));

        quoteHttpHelper.fetchQuoteInfo(symbol);
    }
}
