import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database configuration - UPDATE THESE VALUES
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/ridebookingapp";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "10242";

    private static Connection connection = null;

    // Private constructor to prevent instantiation
    private DatabaseConnection() {
    }

    /**
     * Get the database connection (singleton pattern)
     * 
     * @return Connection object
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                // Load the PostgreSQL JDBC driver
                Class.forName("org.postgresql.Driver");

                // Establish the connection
                connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("Database connected successfully!");

            } catch (ClassNotFoundException e) {
                System.err.println("PostgreSQL JDBC Driver not found!");
                System.err.println("Make sure postgresql-42.7.3.jar is in your classpath.");
                throw new RuntimeException(e);
            } catch (SQLException e) {
                System.err.println("Database connection failed!");
                System.err.println("Error: " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
        return connection;
    }

    /**
     * Close the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }

    /**
     * Check if connected to database
     * 
     * @return true if connected, false otherwise
     */
    public static boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
