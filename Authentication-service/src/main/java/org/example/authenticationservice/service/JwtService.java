package org.example.authenticationservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.example.authenticationservice.entity.UserCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

  @Value("${library.jwt.secret}")
  private String secretKey;

  @Value("${library.jwt.expiration}")
  private long jwtExpiration;

  @Value("${library.jwt.refresh-expiration:604800000}")
  private long refreshExpiration;

  private SecretKey getSignKey() {
    byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String buildToken(Map<String, Object> extraClaims, UserCredentials userCredentials, long expiration) {
    return Jwts.builder()
            .claims(extraClaims)
            .subject(userCredentials.getUsername())
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSignKey())
            .compact();
  }

  public String generateAccessToken(UserCredentials userCredentials) {
    log.info("Generating Access Token for user: {}", userCredentials.getUsername());
    Map<String, Object> claims = new HashMap<>();
    claims.put("userId", userCredentials.getUserId());
    claims.put("role", userCredentials.getRole().name());
    return buildToken(claims, userCredentials, jwtExpiration);
  }

  public String generateRefreshToken(UserCredentials userCredentials) {
    log.info("Generating Refresh Token for user: {}", userCredentials.getUsername());
    return buildToken(new HashMap<>(), userCredentials, refreshExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String extractUsername(String token) {
    return extractClaim(token, claims -> claims.get("sub", String.class));
  }

  public Long extractUserId(String token) {
    return extractClaim(token, claims -> claims.get("userId", Long.class));
  }

  public String extractRole(String token) {
    return extractClaim(token, claims -> claims.get("role", String.class));
  }

  public boolean isTokenValid(String token, UserCredentials userCredentials) {
    final String username = extractUsername(token);
    return (username.equals(userCredentials.getUsername())) && !isTokenExpired(token);
  }

  public boolean validateToken(String token) {
    try {
      extractAllClaims(token);
      return !isTokenExpired(token);
    } catch (Exception e) {
      log.error("JWT Validation failed: {}", e.getMessage());
      return false;
    }
  }

  private boolean isTokenExpired(String token) {
    return extractClaim(token, claims -> claims.getExpiration()).before(new Date());
  }
}