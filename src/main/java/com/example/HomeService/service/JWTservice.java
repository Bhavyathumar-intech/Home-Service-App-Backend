//package com.example.HomeService.service;
//
//
//import java.security.Key;
//import java.security.NoSuchAlgorithmException;
//import java.util.Base64;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.function.Function;
//
//import javax.crypto.KeyGenerator;
//import javax.crypto.SecretKey;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Service;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//
//@Service
//public class JWTservice {
////	@Value("${jwt.secret}")
////    private String secretKey;
////
////	public String generateToken(String email) {
////		Map<String,Object>claims = new HashMap<>();
////
////		return Jwts.builder()
////				.claims()
////				.add(claims)
////				.subject(email)
////				.issuedAt(new Date(System.currentTimeMillis()))
////				.expiration(new Date(System.currentTimeMillis() + 60*60*60*30))
////				.and()
////				.signWith(getKey())
////				.compact();
////	}
//
//	private String secretKey = "";
//
//	public JWTservice() {
//		try {
//			KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
//			SecretKey sk = keygen.generateKey();
//			secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
//		} catch (NoSuchAlgorithmException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	public String generateToken(String username) {
//		Map<String, Object> claims = new HashMap<>();
//		return Jwts.builder()
//				.claims()
//				.add(claims)
//				.subject(username)
//				.issuedAt(new Date(System.currentTimeMillis()))
//				.expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30))
//				.and()
//				.signWith(getKey())
//				.compact();
//	}
//
//	private Key getKey() {
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        return Keys.hmacShaKeyFor(keyBytes);
//    }
//
//	public String extractEmail(String token) {
//		   return extractClaim(token, Claims::getSubject);
//	}
//
//    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
//	        final Claims claims = extractAllClaims(token);
//	        return claimResolver.apply(claims);
//	}
//
//	private Claims extractAllClaims(String token) {
//	        return Jwts.parser()
//	                .verifyWith((SecretKey) getKey())
//	                .build()
//	                .parseSignedClaims(token)
//	                .getPayload();
//	}
//
//	private boolean isTokenExpired(String token) {
//	        return extractExpiration(token).before(new Date());
//	}
//
//	private Date extractExpiration(String token) {
//	        return extractClaim(token, Claims::getExpiration);
//	}
//
//	public boolean validateToken(String token, UserDetails userDetails) {
//		final String email = extractEmail(token);
//		System.out.println("true");
//		System.out.println((email.equals(userDetails.getUsername()) && !isTokenExpired(token)));
//		return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
//	}
//
//
//}

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

@Service
public class JWTservice {

	private String secretKey = "";

	public JWTservice() {
		try {
			KeyGenerator keygen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keygen.generateKey();
			secretKey = Base64.getEncoder().encodeToString(sk.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	// ✅ Generate token with user details
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
				.expiration(new Date(System.currentTimeMillis() + 60 * 60 * 30 * 1000)) // Expiration time
				.signWith(getKey())
				.compact();
	}

	private Key getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith((SecretKey) getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String email = extractEmail(token);
		return (email.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}
}
