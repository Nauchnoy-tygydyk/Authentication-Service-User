package org.example.paymentservice.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
@Value("${library.jwt.secret}")
private String secretKey;

private SecretKey getSignKey() {
  byte[] keyBytes = Decoders.BASE64.decode(this.secretKey);
  return Keys.hmacShaKeyFor(keyBytes);
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

public boolean validateToken(String token) {
  try {
    extractAllClaims(token);
    return !isTokenExpired(token);
  } catch (Exception e) {

    return false;
  }
}

private boolean isTokenExpired(String token) {
  return extractClaim(token, Claims::getExpiration).before(new Date());
}
}
