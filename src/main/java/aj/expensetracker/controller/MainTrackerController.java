package aj.expensetracker.controller;

import aj.expensetracker.model.Expense;
import aj.expensetracker.utils.DatabaseConnection;
import aj.expensetracker.utils.UserSession;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.List;

public class MainTrackerController {

    @FXML
    private TableView<Expense> expenseTable;

    @FXML
    private TableColumn<Expense, LocalDate> dateColumn;

    @FXML
    private TableColumn<Expense, String> objectColumn;

    @FXML
    private TableColumn<Expense, Double> priceColumn;

    @FXML
    private TableColumn<Expense, Void> actionColumn;


    @FXML
    private DatePicker dateInput;

    @FXML
    private TextField objectInput;

    @FXML
    private TextField priceInput;

    @FXML
    private TextField totalField;

    @FXML
    private Button addExpenseButton;

    @FXML
    private Button visualizeButton;

    private ObservableList<Expense> expenseList = FXCollections.observableArrayList();


    @FXML
    void handleAddExpense(ActionEvent event) {
        addExpense();
    }

    private void addExpense(){
        LocalDate date = dateInput.getValue();
        String object = objectInput.getText();
        String priceText = priceInput.getText();

        if (date != null && !object.isEmpty() && !priceText.isEmpty()) {
            try {
                double price = Double.parseDouble(priceText);
                Expense expense = new Expense(date, object, price, UserSession.getInstance().getUsername());
                // Add expense to database here
                ExpenseController.createExpense(expense);

                loadExpensesFromDatabase();

                // Clear input fields
                objectInput.clear();
                priceInput.clear();
                totalField.setText(ExpenseController.getTotal()+"");
                // Optionally refresh the table view here
            } catch (NumberFormatException e) {
                // Handle invalid price input
                System.out.println("Invalid price format.");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            // Handle empty fields
            System.out.println("Please fill in all fields.");
        }
    }

    @FXML
    public void initialize() throws SQLException {
        // Initialize the table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        objectColumn.setCellValueFactory(new PropertyValueFactory<>("object"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Load expenses and set them to the TableView
        loadExpensesFromDatabase();
        dateInput.setValue(LocalDate.now());

        totalField.setText(ExpenseController.getTotal()+"");

        actionColumn.setCellFactory(param -> new TableCell<Expense, Void>() {
            private final Button deleteButton = new Button();
            URL url = getClass().getResource("/aj/expensetracker/assets/trash-bin.png");
//            {
//                try {
//                    url = new File("src/main/java/aj/expensetracker/assets/trash-bin.png").toURI().toURL();
//                } catch (MalformedURLException e) {
//                    System.out.println("Delete image not found.");
//                    throw new RuntimeException(e);
//                }
//            }
            private final ImageView trashIcon = new ImageView(new Image(String.valueOf(url)));

            {
                // Set the icon to the button
                trashIcon.setFitHeight(20);  // Adjust the size of the icon
                trashIcon.setFitWidth(20);
                deleteButton.setGraphic(trashIcon);
                deleteButton.setStyle("-fx-background-color: transparent;");

                deleteButton.setOnAction(event -> {
                    Expense expense = getTableView().getItems().get(getIndex());
                    try {
                        ExpenseController.deleteExpense(expense.getId());  // Method to delete the expense
                        loadExpensesFromDatabase();
                    } catch (SQLException e) {
                        System.out.println("Error deleting expense.");
                        throw new RuntimeException(e);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                    setAlignment(Pos.CENTER);  // Center the button in the cell
                }
            }
        });

        // Set focus on the next field when Enter is pressed in the nameField

        // Set focus on the next field when Enter is pressed in the amountField
        objectInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (objectInput.getText().isEmpty()) {
                    showAlert("Mandatory field.", "Enter object.");
                }else priceInput.requestFocus();  // Move to the categoryField
            }
        });

        // Optionally, you can handle the categoryField too
        priceInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                // If this is the last field, you can submit the form or do something else
                if (priceInput.getText().isEmpty()) {
                    showAlert("Mandatory field.", "Enter price.");
                }else{
                    addExpense();  // Call your submit method
                    objectInput.requestFocus();
                }
            }
            if (priceInput.getText().isEmpty() && event.getCode() == KeyCode.BACK_SPACE) {
                objectInput.requestFocus();

            }
        });
    }

    @FXML
    private void handleVisualizeData(ActionEvent event) {
        try {
            URL url = getClass().getResource("/aj/expensetracker/DataVisualization.fxml");
            Parent visualizationScreen = FXMLLoader.load(url);

            // Switch to the new scene
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(visualizationScreen);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to load expenses
    public void loadExpensesFromDatabase() {
        List<Expense> expenses = null;
        try {
            expenses = ExpenseController.getAllExpenses();
        } catch (SQLException e) {
            System.out.println("Error getting expenses from database.");
            throw new RuntimeException(e);
        }
        expenseTable.getItems().setAll(expenses); // Add expenses to the TableView
        expenseTable.sort();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}

