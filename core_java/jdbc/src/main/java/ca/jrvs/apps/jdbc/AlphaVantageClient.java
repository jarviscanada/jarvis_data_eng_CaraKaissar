package ca.jrvs.apps.jdbc;


import java.net.URI;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AlphaVantageClient {

    private static final String API_KEY = "70e344c139msh8b96a0b085f24bfp1ae4c4jsn8a14887b51c5";  // Replace with your actual API key


    public static void fetchStockQuote(String symbol) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://alpha-vantage.p.rapidapi.com/query?function=GLOBAL_QUOTE&symbol="+symbol+"&datatype=json"))
                .header("X-RapidAPI-Key", API_KEY)
                .header("X-RapidAPI-Host", "alpha-vantage.p.rapidapi.com")
                .GET()
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        fetchStockQuote("MSFT");  // Example: Get quote for Microsoft
    }
}
