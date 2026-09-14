package org.example.apigateway.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.function.Function;

@Component
public class JwtUtil {

  @Value("${jwt.secret}")
  private String secret;

  private SecretKey getSignKey(){
      byte []Keybytes = Decoders.BASE64.decode(this.secret);
      return Keys.hmacShaKeyFor(Keybytes);
  }

  public void validateToken(final String token){
    Jwts.parser()
            .verifyWith(getSignKey())
            .build()
            .parseSignedClaims(token);
  }

    private io.jsonwebtoken.Claims extractAllClaims(final String token){
      return Jwts.parser()
              .verifyWith(getSignKey())
              .build()
              .parseSignedClaims(token)
              .getPayload();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
      final Claims claims = extractAllClaims(token);
      return claimsResolver.apply(claims);
    }

    public String extractUserId(String token){
    return extractClaim(token, claims -> claims.get("userId", Object.class).toString());
    }

  public String extractRole(String token){
    return extractClaim(token, claims -> claims.get("role", Object.class).toString());
  }

}
