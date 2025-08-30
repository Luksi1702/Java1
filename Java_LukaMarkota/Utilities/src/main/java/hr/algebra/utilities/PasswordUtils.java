/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hr.algebra.utilities;

/**
 *
 * @author lukam
 */
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.MessageDigest;
import java.util.Base64;

public class PasswordUtils {

    // Helper class to hold the result
    public static class HashSalt {

        public final String hash;
        public final String salt;

        public HashSalt(String hash, String salt) {
            this.hash = hash;
            this.salt = salt;
        }
    }

    // Method to generate salt and hash
    public static HashSalt hashPassword(String password) throws NoSuchAlgorithmException {
        // Generate salt
        byte[] saltBytes = new byte[16];
        SecureRandom sr = SecureRandom.getInstanceStrong();
        sr.nextBytes(saltBytes);

        // Hash the password with the salt
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(saltBytes); // Add salt to digest
        byte[] hashBytes = md.digest(password.getBytes());

        // Encode to base64 for storage/transmission
        String saltBase64 = Base64.getEncoder().encodeToString(saltBytes);
        String hashBase64 = Base64.getEncoder().encodeToString(hashBytes);

        return new HashSalt(hashBase64, saltBase64);
    }

    // Hash a password with an existing salt (used during login)
    public static String hashPassword(String password, String saltBase64) throws NoSuchAlgorithmException {
        byte[] saltBytes = Base64.getDecoder().decode(saltBase64);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(saltBytes); // Add the decoded salt
        byte[] hashBytes = md.digest(password.getBytes());

        return Base64.getEncoder().encodeToString(hashBytes);
    }

}
