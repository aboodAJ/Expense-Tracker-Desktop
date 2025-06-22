package aj.expensetracker.tests;
import aj.expensetracker.controller.ExpenseController;
import aj.expensetracker.model.Expense;
import aj.expensetracker.utils.UserSession;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;


public class ExpenseTest {
    public static void main(String[] args) {
        ExpenseController expenseController = new ExpenseController();

        // Create an expense
        try {
            Expense newExpense = new Expense(0, LocalDate.now(), "Food", 29.99, UserSession.getInstance().getUsername());
            expenseController.createExpense(newExpense);
            System.out.println("Expense created.");

            // Read all expenses
            List<Expense> expenses = expenseController.getAllExpenses();
            System.out.println("All expenses: " + expenses);

            // Update an expense (just an example; adjust ID accordingly)
            if (!expenses.isEmpty()) {
                Expense expenseToUpdate = expenses.get(0);
                expenseToUpdate.setPrice(35.00);
                expenseController.updateExpense(expenseToUpdate);
                System.out.println("Expense updated.");
            }

            // Delete an expense (just an example; adjust ID accordingly)
            if (!expenses.isEmpty()) {
                expenseController.deleteExpense(expenses.get(0).getId());
                System.out.println("Expense deleted.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
