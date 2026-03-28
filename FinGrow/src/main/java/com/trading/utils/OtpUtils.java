package com.trading.utils;

import java.security.SecureRandom;

public class OtpUtils {

    private static final SecureRandom random = new SecureRandom();

    public static String generateOTP() {
        int length = 6;

        StringBuilder otp = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10));
        }

        return otp.toString();
    }
}