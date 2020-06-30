package com.raghu.iot.consumer.restapi.utils;

import java.util.Base64;

public class TokenUtil {

    public static final String ADMIN = "admin";
    public static boolean validateToken(final String token) {
        byte[] actualByte = Base64.getDecoder().decode(token);
        String actualString = new String(actualByte);
        return actualString.equals(ADMIN);
    }

    public static String generateToken(String user) {
        return Base64.getEncoder().encodeToString(user.getBytes());
    }
}
