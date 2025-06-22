package aj.expensetracker.controller;

import aj.expensetracker.utils.DatabaseConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.util.Duration;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MonthDetailController {

    @FXML
    private LineChart<Number, Number> dailyExpenseChart;

    @FXML
    private NumberAxis dayAxis;

    @FXML
    private NumberAxis expenseAxis;

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            URL url = getClass().getResource("/aj/expensetracker/DataVisualization.fxml");
            Parent visualizationScreen = FXMLLoader.load(url);

            // Switch to the Data Visualization screen
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(visualizationScreen);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initialize() {
        // Ensure the dayAxis is configured correctly
        dayAxis.setTickUnit(1); // Ensure it increments by 1
        dayAxis.setMinorTickCount(0); // No minor ticks
        dayAxis.setForceZeroInRange(false);
        dayAxis.setAutoRanging(false); // We'll set the range dynamically
    }

    public void loadData(int month, int year) {
        String query = "SELECT date, SUM(price) as total FROM expenses WHERE MONTH(date) = ? AND YEAR(date) = ? GROUP BY date";

        Map<Integer, Double> dailyExpenses = new HashMap<>();
        int maxDay = 0;

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, month);
            pstmt.setInt(2, year);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("date").toLocalDate();
                    double total = rs.getDouble("total");
                    int day = date.getDayOfMonth();
                    dailyExpenses.put(date.getDayOfMonth(), total);

                    if (day > maxDay) {
                        maxDay = day; // Track the last day of the month
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set the range for the X-axis
        dayAxis.setLowerBound(1);
        dayAxis.setUpperBound(maxDay);

        populateChart(dailyExpenses, maxDay);
    }


    private void populateChart(Map<Integer, Double> dailyExpenses, int maxDay) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Daily Expenses");


        Platform.runLater(() -> {
            for (int day = 1; day <= maxDay; day++) {
                double expense = dailyExpenses.getOrDefault(day, 0.0);
                XYChart.Data<Number, Number> dataPoint = new XYChart.Data<>(day, expense);
                series.getData().add(dataPoint);
                Node node = dataPoint.getNode();

                // Create and add tooltip
                Tooltip tooltip = new Tooltip("Day: " + day + "\nExpense: $" + expense);
                tooltip.setShowDelay(Duration.ZERO);
                Tooltip.install(node, tooltip);

                // Optional: Add hover effects
                if (node != null) {
                    node.setOnMouseEntered(event -> {
                        dataPoint.getNode().setStyle("-fx-scale-x: 1.2; -fx-scale-y: 1.2; -fx-background-color: #ff6347;");
                    });
                    node.setOnMouseExited(event -> {
                        dataPoint.getNode().setStyle("");
                    });
                }

            }
        });

        // Update the chart data
        dailyExpenseChart.getData().clear();
        dailyExpenseChart.getData().add(series);
    }

}