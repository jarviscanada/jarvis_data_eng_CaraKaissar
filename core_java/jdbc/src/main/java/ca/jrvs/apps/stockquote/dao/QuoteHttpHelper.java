package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.models.Quote;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;


public class QuoteHttpHelper {

    private String apiKey ;
    OkHttpClient client;

    public QuoteHttpHelper(String key) {
        this.client = new OkHttpClient();
        this.apiKey = key;
    }
    /**
     * Fetch latest quote data from Alpha Vantage endpoint
     * @param symbol
     * @return Quote with latest data
     * @throws IllegalArgumentException - if no data was found for the given symbol
     */
    public Quote fetchQuoteInfo(String symbol) throws IllegalArgumentException {
        if (symbol == null || symbol.isEmpty()) {
            throw new IllegalArgumentException("Invalid ticker symbol.");
        }

        String url = "https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol=" + symbol + "&datatype=json";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-RapidAPI-Key", apiKey)
                .addHeader("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String responseBody = response.body().string();

            // Deserialize JSON using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            if (!jsonNode.has("Global Quote")) {
                throw new IllegalArgumentException("No data found for symbol: " + symbol);
            }

            JsonNode quoteNode = jsonNode.get("Global Quote");

            // Create a Quote object
            Quote quote = new Quote();

            // Map individual fields from the JSON response
            quote.setTicker(quoteNode.get("01. symbol").asText());  // Set the ticker
            quote.setOpen(quoteNode.get("02. open").asDouble());
            quote.setHigh(quoteNode.get("03. high").asDouble());
            quote.setLow(quoteNode.get("04. low").asDouble());
            quote.setPrice(quoteNode.get("05. price").asDouble());
            quote.setVolume(quoteNode.get("06. volume").asInt());
            quote.setLatestTradingDay(Date.valueOf(quoteNode.get("07. latest trading day").asText()));
            quote.setPreviousClose(quoteNode.get("08. previous close").asDouble());
            quote.setChange(quoteNode.get("09. change").asDouble());
            quote.setChangePercent(quoteNode.get("10. change percent").asText());
            quote.setTimestamp(new Timestamp(System.currentTimeMillis()));

            return quote;

        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error fetching data for symbol: " + symbol, e);
        }
    }

}
