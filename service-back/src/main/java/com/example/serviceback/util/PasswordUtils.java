package com.example.serviceback.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @author CJW
 * @since 2024/3/23
 */
public class PasswordUtils {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static String encode(String rawPwd) {
        return passwordEncoder.encode(rawPwd);
    }

    public static boolean match(String rawPwd, String encodePwd) {
        return passwordEncoder.matches(rawPwd, encodePwd);
    }
}
