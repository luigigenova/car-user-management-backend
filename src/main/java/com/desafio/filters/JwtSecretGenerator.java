package com.desafio.filters;

import java.util.Base64;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class JwtSecretGenerator {
    public static void main(String[] args) throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
        keyGen.init(256);
        SecretKey key = keyGen.generateKey();
        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
        System.out.println("Generated Key: " + base64Key);
    }
}
