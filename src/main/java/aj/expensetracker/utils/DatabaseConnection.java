package aj.expensetracker.utils;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.*;

public class DatabaseConnection {
    private static final Dotenv dotenv = Dotenv.load();

    private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker_db";
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");


    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.out.println("Connection to DB failed");
            throw new RuntimeException(e);
        }
    }

    // Method to check database connection
    public static boolean isDatabaseConnected() {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            return true; // Connection successful
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            return false;
        }
    }

    // Method to check if a table exists
    private static boolean isTablePresent(String tableName) {
        String query = "SHOW TABLES LIKE ?";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, tableName);
            ResultSet rs = pstmt.executeQuery();
            return rs.next(); // Returns true if the table exists

        } catch (SQLException e) {
            System.out.println("Error checking table '" + tableName + "': " + e.getMessage());
            return false;
        }
    }

    // Method to create the users table if it doesn't exist
    private static void createUsersTable() {
        String createTableSQL = """
            CREATE TABLE users (
                username VARCHAR(50) PRIMARY KEY,
                hashed_password VARCHAR(255) NOT NULL
            )
        """;

        executeTableCreation(createTableSQL, "users");
    }

    // Method to create the expenses table if it doesn't exist
    private static void createExpensesTable() {
        String createTableSQL = """
            CREATE TABLE expenses (
                id INT PRIMARY KEY AUTO_INCREMENT,
                object VARCHAR(255) NOT NULL,
                price DOUBLE NOT NULL,
                date DATE NOT NULL,
                username VARCHAR(50),
                FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
            )
        """;

        executeTableCreation(createTableSQL, "expenses");
    }

    // Method to execute table creation queries
    private static void executeTableCreation(String query, String tableName) {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate(query);
            System.out.println("Table '" + tableName + "' created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating table '" + tableName + "': " + e.getMessage());
        }
    }

    // Method to verify the database setup
    public static void verifyDatabase() {
        if (!isDatabaseConnected()) {
            System.out.println("Cannot proceed. Database connection failed.");
            return;
        }

        if (!isTablePresent("users")) {
            System.out.println("Table 'users' not found. Creating...");
            createUsersTable();
        } else {
            System.out.println("Table 'users' is ready.");
        }

        if (!isTablePresent("expenses")) {
            System.out.println("Table 'expenses' not found. Creating...");
            createExpensesTable();
        } else {
            System.out.println("Table 'expenses' is ready.");
        }
    }
}
