package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.models.Quote;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuoteDao implements CrudDao<Quote, String> {

    private Connection connection;
    private static final String INSERT =  "INSERT INTO quote (symbol, open, high, low, price, volume, latest_trading_day, previous_close, change, change_percent, timestamp) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ";
    private static final String GET_ONE = "SELECT * FROM quote WHERE symbol = ?";
    private static final String GET_ALL = "SELECT * FROM quote";
    private static final String DELETE = "DELETE FROM quote WHERE symbol = ?";
    private static final String DELETE_ALL = "DELETE FROM quote";

    public QuoteDao(Connection connection) throws SQLException {
        this.connection = connection;
    }


    @Override
    public Quote save(Quote quote) throws IllegalArgumentException {

        try (PreparedStatement ps = connection.prepareStatement(INSERT)) {
            ps.setString(1, quote.getTicker());
            ps.setDouble(2, quote.getOpen());
            ps.setDouble(3, quote.getHigh());
            ps.setDouble(4, quote.getLow());
            ps.setDouble(5, quote.getPrice());
            ps.setInt(6, quote.getVolume());
            ps.setDate(7, new java.sql.Date(quote.getLatestTradingDay().getTime()));
            ps.setDouble(8, quote.getPreviousClose());
            ps.setDouble(9, quote.getChange());
            ps.setString(10, quote.getChangePercent());
            if (quote.getTimestamp() == null) {
                ps.setTimestamp(11, new Timestamp(System.currentTimeMillis())); // Default to current time if null
            } else {
                ps.setTimestamp(11, quote.getTimestamp());
            }

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error saving quote: " + e.getMessage(), e);
        }

        return quote;
    }

    @Override
    public Optional<Quote> findById(String symbol) throws IllegalArgumentException {
        Quote quote = null;
        try (PreparedStatement ps = connection.prepareStatement(GET_ONE)) {
            ps.setString(1, symbol);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                quote = new Quote();
                quote.setTicker(rs.getString("symbol"));
                quote.setOpen(rs.getDouble("open"));
                quote.setHigh(rs.getDouble("high"));
                quote.setLow(rs.getDouble("low"));
                quote.setPrice(rs.getDouble("price"));
                quote.setVolume(rs.getInt("volume"));
                quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
                quote.setPreviousClose(rs.getDouble("previous_close"));
                quote.setChange(rs.getDouble("change"));
                quote.setChangePercent(rs.getString("change_percent"));
                quote.setTimestamp(rs.getTimestamp("timestamp"));
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error finding quote by symbol: " + e.getMessage(), e);
        }

        return Optional.ofNullable(quote);
    }

    @Override
    public Iterable<Quote> findAll() {
        List<Quote> quotes = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ALL)) {

            while (rs.next()) {
                Quote quote = new Quote();
                quote.setTicker(rs.getString("symbol"));
                quote.setOpen(rs.getDouble("open"));
                quote.setHigh(rs.getDouble("high"));
                quote.setLow(rs.getDouble("low"));
                quote.setPrice(rs.getDouble("price"));
                quote.setVolume(rs.getInt("volume"));
                quote.setLatestTradingDay(rs.getDate("latest_trading_day"));
                quote.setPreviousClose(rs.getDouble("previous_close"));
                quote.setChange(rs.getDouble("change"));
                quote.setChangePercent(rs.getString("change_percent"));
                quote.setTimestamp(rs.getTimestamp("timestamp"));

                quotes.add(quote);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all quotes: " + e.getMessage(), e);
        }

        return quotes;
    }

    @Override
    public void deleteById(String symbol) throws IllegalArgumentException {
        try (PreparedStatement ps = connection.prepareStatement(DELETE)) {
            ps.setString(1, symbol);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error deleting quote by symbol: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            connection.setAutoCommit(false); // Start transaction

            // First delete all positions that reference quotes
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM position");
            }

            // Then delete all quotes
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("DELETE FROM quote");
            }

            connection.commit(); // Commit transaction

        } catch (SQLException e) {
            try {
                connection.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                throw new RuntimeException("Error during transaction rollback: " + rollbackEx.getMessage(), rollbackEx);
            }
            throw new RuntimeException("Error deleting all quotes: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true); // Restore auto-commit
            } catch (SQLException e) {
                throw new RuntimeException("Error restoring auto-commit mode: " + e.getMessage(), e);
            }
        }
    }

}
