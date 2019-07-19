package com.codecool.krk.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordHasher {
    public PasswordHasher() {
    }

    public String hashPassword(String originalString) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return bytesToHex(digest.digest(originalString.getBytes(StandardCharsets.UTF_8)));
    }

    public String getRandomSalt(final int size){
        final SecureRandom secureRandom=new SecureRandom();
        final byte[] bytes=new byte[size];
        secureRandom.nextBytes(bytes);
        return bytesToHex(bytes);
    }

    private String bytesToHex(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
