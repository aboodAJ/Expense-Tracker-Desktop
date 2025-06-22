module aj.expensetracker {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires bcrypt;
    requires java.dotenv;


    opens aj.expensetracker to javafx.fxml;
    opens aj.expensetracker.controller to javafx.fxml;
    opens aj.expensetracker.model to javafx.base;
    exports aj.expensetracker;
    exports aj.expensetracker.controller;
}