package aj.expensetracker.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class Expense {
    private int id;
    private double price;
    private String object;
    private LocalDate date;
    private String userID;

    // Constructors
    public Expense(int id, LocalDate date, String object, double price, String userID) {
        this.id = id;
        this.price = price;
        this.object = object;
        this.date = date;
        this.userID = userID;
    }

    public Expense(LocalDate date, String object, double price, String userID) {
        this.price = price;
        this.object = object;
        this.date = date;
        this.userID = userID;
    }

    // Getters and Setters
    public int getId() { return id; }
    public double getPrice() { return price; }
    public String getObject() { return object; }
    public LocalDate getDate() { return date; }
    public String getUserID() { return userID; }

    public void setId(int id) { this.id = id; }
    public void setPrice(double price) { this.price = price; }
    public void setObject(String object) { this.object = object; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setUserID(String userID) { this.userID = userID; }
}