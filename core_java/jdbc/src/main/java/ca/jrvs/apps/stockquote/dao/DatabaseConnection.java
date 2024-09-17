package ca.jrvs.apps.stockquote.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection {
Logger logger = LoggerFactory.getLogger(DatabaseConnection.class);

    private String dbUrl;
    private String dbUser;
    private String dbPassword;

    public DatabaseConnection(String dbUrl, String dbUser, String dbPassword) {
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
    }

    public Connection getConnection() throws SQLException {
        logger.info("Attempting to establish a database connection to: {}", dbUrl);
        Properties properties = new Properties();
        properties.setProperty("user", dbUser);
        properties.setProperty("password", dbPassword);
        try {
            Connection connection = DriverManager.getConnection(dbUrl, properties);
            logger.info("Database connection established successfully to: {}", dbUrl);
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to establish a connection to the database at {}: {}", dbUrl, e.getMessage());
            throw new RuntimeException("Error establishing database connection", e);
        }
    }
}
