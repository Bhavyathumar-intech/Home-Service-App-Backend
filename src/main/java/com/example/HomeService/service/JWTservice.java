package com.example.HomeService.service;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Service class for handling JWT token generation and validation.
 */
@Service
public class JWTservice {

    private String secretKey = "";

    /**
     * Constructor to generate a secret key for JWT signing.
     */
    public JWTservice() {
        try {
            KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keygen.generateKey();
            secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a JWT token with user details.
     *
     * @param email             User email (subject of the token)
     * @param role              User role
     * @param userId            Unique ID of the user
     * @param serviceProviderId Unique ID of the service provider (optional)
     * @return Generated JWT token
     */
    public String generateToken(String email, String role, Long userId, Long serviceProviderId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);
        if (serviceProviderId != null) {
            claims.put("serviceProviderId", serviceProviderId);
        }

        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000)) // 24 Hr. Expiration time
                .signWith(getKey())
                .compact();
    }

    /**
     * Retrieves the secret key for JWT signing.
     *
     * @return Key instance for signing JWTs
     */
    private Key getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Extracts the email (subject) from a given JWT token.
     *
     * @param token JWT token
     * @return Extracted email
     */
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts a specific claim from a JWT token.
     *
     * @param token         JWT token
     * @param claimResolver Function to extract a specific claim
     * @param <T>           The type of claim
     * @return Extracted claim value
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    /**
     * Extracts all claims from a JWT token.
     *
     * @param token JWT token
     * @return Extracted claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Checks if a token has expired.
     *
     * @param token JWT token
     * @return True if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from a JWT token.
     *
     * @param token JWT token
     * @return Expiration date of the token
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Validates a JWT token against user details.
     *
     * @param token       JWT token
     * @param userDetails User details to validate against
     * @return True if the token is valid, false otherwise
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
