package ca.jrvs.apps.stockquote.dao.services;

import ca.jrvs.apps.stockquote.dao.QuoteHttpHelper;
import ca.jrvs.apps.stockquote.dao.models.Quote;
import ca.jrvs.apps.stockquote.dao.QuoteDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class QuoteService {

    Logger logger = LoggerFactory.getLogger(QuoteService.class);

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
        logger.info("fetchQuoteDataFromAPI ticker: " + ticker);

        if (ticker == null || ticker.isEmpty() || !ticker.matches("^[A-Za-z]{1,5}$")) {
            logger.error("Invalid ticker: " + ticker);
            throw new IllegalArgumentException("Invalid ticker symbol: " + ticker);
        }

        Quote quote = httpHelper.fetchQuoteInfo(ticker);

        if (quote == null || !quote.getTicker().equals(ticker)) {
            logger.warn("Quote data not found or ticket mismatch: " + ticker);
            return Optional.empty();
        }

        dao.save(quote);
        logger.info("Quote saved for ticket " + ticker);

        return Optional.of(quote);
    }


    /**
     * Saves a quote to the database.
     * @param quote - The Quote object to be saved.
     */
    public void saveQuote(Quote quote) {
        logger.info("Saving quote for ticker: {}", quote.getTicker());
        dao.save(quote);
        logger.info("Quote saved for ticker: {}", quote.getTicker());
    }

    /**
     * Checks if a quote already exists in the database.
     * @param symbol - Stock ticker symbol.
     * @return true if the quote exists, false otherwise.
     */
    public boolean quoteExists(String symbol) {
        logger.info("Checking if quote exists for ticker: {}", symbol);
        boolean exists = dao.findById(symbol).isPresent();
        logger.info("Quote existence check for ticker {}: {}", symbol, exists);
        return exists;
    }

}
