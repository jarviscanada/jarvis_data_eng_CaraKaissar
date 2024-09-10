package ca.jrvs.apps.stockquote.dao;

import ca.jrvs.apps.stockquote.dao.models.Position;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PositionDao implements CrudDao<Position, String> {

    private Connection connection;

    public PositionDao() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    @Override
    public Position save(Position position) throws IllegalArgumentException {
        String sql = "INSERT INTO position (symbol, number_of_shares, value_paid) " +
                "VALUES (?, ?, ?) " +
                "ON CONFLICT (symbol) DO UPDATE " +
                "SET number_of_shares = EXCLUDED.number_of_shares, value_paid = EXCLUDED.value_paid";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, position.getTicker());
            ps.setInt(2, position.getNumOfShares());
            ps.setDouble(3, position.getValuePaid());

            ps.executeUpdate();
            return position;
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error saving position: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Position> findById(String symbol) throws IllegalArgumentException {
        String sql = "SELECT * FROM position WHERE symbol = ?";
        Position position = null;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, symbol);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                position = new Position();
                position.setTicker(rs.getString("symbol"));
                position.setNumOfShares(rs.getInt("number_of_shares"));
                position.setValuePaid(rs.getDouble("value_paid"));
            }
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error finding position by symbol: " + e.getMessage(), e);
        }

        return Optional.ofNullable(position);
    }

    @Override
    public Iterable<Position> findAll() {
        String sql = "SELECT * FROM position";
        List<Position> positions = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Position position = new Position();
                position.setTicker(rs.getString("symbol"));
                position.setNumOfShares(rs.getInt("number_of_shares"));
                position.setValuePaid(rs.getDouble("value_paid"));

                positions.add(position);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving all positions: " + e.getMessage(), e);
        }

        return positions;
    }

    @Override
    public void deleteById(String symbol) throws IllegalArgumentException {
        String sql = "DELETE FROM position WHERE symbol = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, symbol);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalArgumentException("Error deleting position by symbol: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM position";

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting all positions: " + e.getMessage(), e);
        }
    }
}

