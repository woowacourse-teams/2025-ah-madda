package com.ahmadda.infra.auth.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class HashUtils {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final Charset ENCODING = StandardCharsets.UTF_8;

    private HashUtils() {
    }

    public static String sha256(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("값이 null일 수 없습니다");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(value.getBytes(ENCODING));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(HASH_ALGORITHM + "는 지원하지 않습니다", e);
        }
    }
}
