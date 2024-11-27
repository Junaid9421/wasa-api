package com.wasa.api.config;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

 import org.springframework.security.core.Authentication;
 import org.springframework.stereotype.Component;

 import io.jsonwebtoken.Jwts;
 import io.jsonwebtoken.io.Decoders;
 import io.jsonwebtoken.security.Keys;

  @Component
 public class JwtTokenProvider {

//    private String jwtSecret = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890!@#$%^&*<>?/";
   private String jwtSecret = generateSecretKey();
   private long jwtExpirationDate = 12 * 3600000; // Miliseconds 3600000 ~= 1h
     public String generateSecretKey() {
         // length means (32 bytes are required for 256-bit key)
         int length = 32;

         // Create a secure random generator
         SecureRandom secureRandom = new SecureRandom();

         // Create a byte array to hold the random bytes
         byte[] keyBytes = new byte[length];

         // Generate the random bytes
         secureRandom.nextBytes(keyBytes);

         // Encode the key in Base64 format for easier storage and usage
         return Base64.getEncoder().encodeToString(keyBytes);
     }

     public String generateToken(Authentication authentication) {

         String username = authentication.getName();
         Date currentDate = new Date();
         Date expireDate = new Date(currentDate.getTime() + jwtExpirationDate);

         String token = Jwts.builder()
                 .subject(username)
                 .issuedAt(new Date())
                 .expiration(expireDate)
                 .signWith(key())
                 .compact();

         return token;
     }

     private Key key() {
         System.out.println(jwtSecret.toCharArray());
         return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
     }

     public String getUsername(String token) {
         return Jwts.parser()
                 .verifyWith((SecretKey) key())
                 .build()
                 .parseSignedClaims(token)
                 .getPayload()
                 .getSubject();
     }

     // validate JWT token
     public boolean validateToken(String token) {
         Jwts.parser()
                 .verifyWith((SecretKey) key())
                 .build()
                 .parse(token);
         return true;

     }
 }
