package aj.expensetracker.model;

public class User {
    private String username;
    private String hashedPassword;

    // Constructors, getters, and setters
    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }
}
