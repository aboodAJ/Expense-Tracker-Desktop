package aj.expensetracker.controller;
import aj.expensetracker.utils.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DataVisualizationController {

    @FXML
    private BarChart<String, Number> expenseChart;

    // To hold monthly totals
    private Map<Month, Double> monthlyExpenses = new HashMap<>();

    private XYChart.Series<String, Number> series = new XYChart.Series<>();

    @FXML
    private void initialize() {
        loadExpenseData();
        populateChart();
    }

    @FXML
    void handleReturn(ActionEvent event) {
        try {
            URL url = getClass().getResource("/aj/expensetracker/MainTracker.fxml");
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

    private void loadExpenseData() {
        String query = "SELECT object, price, date FROM expenses";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LocalDate date = rs.getDate("date").toLocalDate();
                Month month = date.getMonth();
                double price = rs.getDouble("price");

                // Accumulate expense totals for each month
                monthlyExpenses.put(month, monthlyExpenses.getOrDefault(month, 0.0) + price);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void populateChart() {
        series.setName("Monthly Expenses");

        // Add monthly expenses to the chart
        for(Month month : Month.values()) {
            Double expense = monthlyExpenses.get(month); // Get the expense for the current month
            if (expense == null) {
                expense = 0.0; // Default value if no data for the month
            }
            series.getData().add(new XYChart.Data<>(month.name(), expense));
        }

        Platform.runLater(() -> {
            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    // Create the tooltip with the Y-value (expense total)
                    Tooltip tooltip = new Tooltip("Expense: $" + data.getYValue().toString());

                    // Make the tooltip shows immediately
                    tooltip.setShowDelay(Duration.ZERO);

                    // Add tooltip to the Node (the bar)
                    Tooltip.install(node, tooltip);

                    // Add custom styling for hover (optional)
                    node.setOnMouseEntered(event -> {
                        node.setStyle("-fx-bar-fill: #ff6347;"); // Change color on hover
                    });

                    node.setOnMouseExited(event -> {
                        node.setStyle(""); // Reset to original style on hover exit
                    });
                }

                node.setOnMouseClicked(event -> {
                    String monthName = data.getXValue(); // Get the clicked month name
                    Month month = Month.valueOf(monthName.toUpperCase()); // Convert to Month enum

                    // Load the new screen with the details for the selected month
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/aj/expensetracker/MonthDetail.fxml"));
                        Parent monthDetailScreen = loader.load();

                        // Get the controller and pass the selected month data
                        MonthDetailController controller = loader.getController();
                        controller.loadData(month.getValue(), Year.now().getValue());

                        Stage stage = (Stage) expenseChart.getScene().getWindow();
                        Scene scene = new Scene(monthDetailScreen);
                        stage.setScene(scene);
                        stage.show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });

        expenseChart.getData().add(series);
    }
}
