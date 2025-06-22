package aj.expensetracker.utils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserAuthentication {

    // Hash a password
    public static String hashPassword(String plainPassword) {
        return BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray());
    }

    // Check if the entered password matches the stored hash
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        BCrypt.Result result = BCrypt.verifyer().verify(plainPassword.toCharArray(), hashedPassword);
        return result.verified;
    }
}
