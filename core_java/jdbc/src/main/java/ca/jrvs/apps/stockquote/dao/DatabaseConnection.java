package ca.jrvs.apps.stockquote.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/stock_quote";
    private static final String USER = "postgres";
    private static final String PASSWORD = "newpassword";

    public static Connection getConnection() throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("user", USER);
        properties.setProperty("password", PASSWORD);

        return DriverManager.getConnection(URL, properties);
    }
}
