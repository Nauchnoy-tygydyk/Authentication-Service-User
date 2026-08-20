package org.example.apigateway.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

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
}
