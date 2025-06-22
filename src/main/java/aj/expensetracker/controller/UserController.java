package aj.expensetracker.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import aj.expensetracker.utils.DatabaseConnection;
import aj.expensetracker.utils.UserAuthentication;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserController {

    // Register a new user
    public void registerUser(String username, String password) throws SQLException {
        String hashedPassword = UserAuthentication.hashPassword(password);
        String query = "INSERT INTO users (username, hashed_password) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashedPassword);
            pstmt.executeUpdate();
        }
    }

    // Authenticate user login
    public boolean authenticateUser(String username, String password) throws SQLException {
        String query = "SELECT hashed_password FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String hashedPassword = rs.getString("hashed_password");
                return UserAuthentication.checkPassword(password, hashedPassword);
            } else {
                return false; // Username not found
            }
        }
    }
}
