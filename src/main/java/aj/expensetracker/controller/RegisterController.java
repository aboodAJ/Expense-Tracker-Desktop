package aj.expensetracker.controller;

import aj.expensetracker.utils.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    public void initialize() {
        // Set focus on the next field when Enter is pressed in the amountField
        usernameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                passwordField.requestFocus();  // Move to the categoryField
            }
        });

        // Optionally, you can handle the categoryField too
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // If this is the last field, you can submit the form or do something else
                handleRegister();  // Call your submit method
            }
            if (passwordField.getText().isEmpty() && event.getCode() == KeyCode.BACK_SPACE) {
                usernameField.requestFocus();
            }
        });
    }

    @FXML
    private void handleRegister() {
        DatabaseConnection.verifyDatabase();

        String username = usernameField.getText();
        String password = passwordField.getText();

        UserController userController = new UserController();
        try {
            userController.registerUser(username, password);
            showAlert("Registration Successful", "User registered successfully.");
            handleBackToLogin(); // Go back to login screen
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Database error occurred.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            URL url = getClass().getResource("/aj/expensetracker/LoginScreen.fxml");
            Parent root = FXMLLoader.load(url);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}