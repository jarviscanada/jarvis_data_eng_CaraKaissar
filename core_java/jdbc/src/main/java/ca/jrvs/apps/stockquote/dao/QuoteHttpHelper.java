package ca.jrvs.apps.stockquote.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;


public class QuoteHttpHelper {

    private String apiKey = "70e344c139msh8b96a0b085f24bfp1ae4c4jsn8a14887b51c5";
    OkHttpClient client;

    public QuoteHttpHelper() {
        this.client = new OkHttpClient();
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

            // Map JSON to Quote object
            Quote quote = objectMapper.treeToValue(quoteNode, Quote.class);

            return quote;

        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Error fetching data for symbol: " + symbol, e);
        }
    }
}
