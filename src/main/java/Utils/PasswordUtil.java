package Utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private static final int LOG_ROUNDS = 10;

    public static String hashPassword(String plain) {
        if (plain == null) return null;
        return BCrypt.hashpw(plain, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean verifyPassword(String hashed, String plain) {
        if (hashed == null || plain == null) return false;
        try {
            if (hashed.startsWith("$2a$") || hashed.startsWith("$2b$") || hashed.startsWith("$2y$")) {
                return BCrypt.checkpw(plain, hashed);
            }

            return hashed.equals(plain);
        } catch (Exception e) {
            return false;
        }
    }
}
