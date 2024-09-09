package ca.jrvs.apps.stockquote.dao.services;

import ca.jrvs.apps.stockquote.dao.QuoteHttpHelper;
import ca.jrvs.apps.stockquote.dao.models.Quote;
import ca.jrvs.apps.stockquote.dao.QuoteDao;

import java.util.Optional;

public class QuoteService {
    private QuoteDao dao;
    private QuoteHttpHelper httpHelper;


      public QuoteService(QuoteDao dao, QuoteHttpHelper httpHelper) {
            this.dao = dao;
            this.httpHelper = httpHelper;
        }


    /**
     * Fetches latest quote data from endpoint
     * @param ticker
     * @return Latest quote information or empty optional if ticker symbol not found
     */
    public Optional<Quote> fetchQuoteDataFromAPI(String ticker) {
        if (ticker == null || ticker.isEmpty() || !ticker.matches("^[A-Za-z]{1,5}$")) {
            throw new IllegalArgumentException("Invalid ticker symbol: " + ticker);
        }

        Quote quote = httpHelper.fetchQuoteInfo(ticker);

        if (quote == null || !quote.getTicker().equals(ticker)) {
            return Optional.empty();
        }

        dao.save(quote);

        return Optional.of(quote);
    }
}
