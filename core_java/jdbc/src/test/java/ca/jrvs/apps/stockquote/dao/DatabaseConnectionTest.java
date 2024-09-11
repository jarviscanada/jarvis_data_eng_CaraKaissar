package ca.jrvs.apps.stockquote.dao;
import org.junit.Before;
import org.junit.Test;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DatabaseConnectionTest {

    private DatabaseConnection dbConnection;

    @Before
    public void setUp() {
        String dbUrl = "jdbc:postgresql://localhost:5432/stock_quote";
        String dbUser = "postgres";
        String dbPassword = "newpassword";
        dbConnection = new DatabaseConnection(dbUrl, dbUser, dbPassword); // Initialize using constructor
    }

    @Test
    public void testConnection() {
        try {
            Connection connection = dbConnection.getConnection(); // Call the non-static method
            assertNotNull(connection);
            connection.close();
        } catch (SQLException e) {
            fail("SQL Exception occurred: " + e.getMessage());
        }
    }
}