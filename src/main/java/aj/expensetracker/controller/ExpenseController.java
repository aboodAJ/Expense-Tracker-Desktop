package aj.expensetracker.controller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import aj.expensetracker.model.Expense;
import aj.expensetracker.model.User;
import aj.expensetracker.utils.DatabaseConnection;
import aj.expensetracker.utils.UserSession;

public class ExpenseController {

    // Create a new expense
    public static void createExpense(Expense expense) throws SQLException {
        String query = "INSERT INTO expenses (price, object, date, username) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setBigDecimal(1, BigDecimal.valueOf(expense.getPrice()));
            pstmt.setString(2, expense.getObject());
            pstmt.setDate(3, java.sql.Date.valueOf(expense.getDate()));
            pstmt.setString(4, UserSession.getInstance().getUsername());
            pstmt.executeUpdate();
        }
    }

    // Read all expenses
    public static List<Expense> getAllExpenses() throws SQLException {
        List<Expense> expenses = new ArrayList<>();
        String query = "SELECT * FROM expenses where username =?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ) {
            pstmt.setString(1, UserSession.getInstance().getUsername());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Expense expense = new Expense(
                        rs.getInt("id"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("object"),
                        rs.getBigDecimal("price").doubleValue(),
                        rs.getString("username")
                );
                expenses.add(expense);
            }
        }
        return expenses;
    }


    public static BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        String query = "SELECT SUM(price) AS total_price FROM expenses";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getBigDecimal("total_price");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    // Update an existing expense
    public void updateExpense(Expense expense) throws SQLException {
        String query = "UPDATE expenses SET price = ?, object = ?, date = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setBigDecimal(1, BigDecimal.valueOf(expense.getPrice()));
            pstmt.setString(2, expense.getObject());
            pstmt.setDate(3, java.sql.Date.valueOf(expense.getDate()));
            pstmt.setInt(4, expense.getId());
            pstmt.executeUpdate();
        }
    }

    // Delete an expense by ID
    public static void deleteExpense(int id) throws SQLException {
        String query = "DELETE FROM expenses WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}