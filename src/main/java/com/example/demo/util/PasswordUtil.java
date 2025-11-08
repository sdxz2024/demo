package com.example.demo.util;

import org.springframework.util.DigestUtils;

public class PasswordUtil {
    public static String encrypt(String password) {
        String slat = "Yurin";
        return DigestUtils.md5DigestAsHex((slat + password).getBytes());
    }

    public static boolean verify(String inputPassword, String dbPassword) {
        return encrypt(inputPassword).equals(dbPassword);
    }
}
