package com.javeriana.component.utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashing {
    public static String hashPassword(String passwordPlaintext) {
        int workload = 12;
        String salt = BCrypt.gensalt(workload);
        String hashedPassword = BCrypt.hashpw(passwordPlaintext, salt);
        return hashedPassword;
    }

    public static boolean checkPassword(String passwordPlaintext, String storedHash) {
        boolean password_verified = false;

        if (null == storedHash || !storedHash.startsWith("$2a$")) {
            throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
        }

        password_verified = BCrypt.checkpw(passwordPlaintext, storedHash);

        return password_verified;
    }
}
