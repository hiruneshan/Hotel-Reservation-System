package ca.seneca.apd545.RXHgrandhotel.security;

import org.mindrot.jbcrypt.BCrypt;

public class BCrtptHasher {


    public String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }


    public boolean verify(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
