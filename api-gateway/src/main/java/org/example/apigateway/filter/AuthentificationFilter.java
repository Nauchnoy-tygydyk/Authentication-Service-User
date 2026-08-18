package org.example.apigateway.filter;


import org.example.apigateway.config.RouteValidator;
import org.example.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.io.ObjectInputFilter;

@Component
public class AuthentificationFilter extends AbstractGatewayFilterFactory<AuthentificationFilter.Config> {

  @Autowired
private RouteValidator routeValidator;

  @Autowired
private JwtUtil jwtUtil;

  public AuthentificationFilter(){
    super(Config.class);
  }

  @Override
  public GatewayFilter apply(Config config){
    return ((exchange, chain) -> {
      if(routeValidator.isSecured.test(exchange.getRequest())){
        HttpHeaders headers = exchange.getRequest().getHeaders();
       if(!headers.containsKey(HttpHeaders.AUTHORIZATION)){
         return onError(exchange, HttpStatus.UNAUTHORIZED, "Missing auth header" );
       }
       String authHeader = headers.get(HttpHeaders.AUTHORIZATION).get(0);
       if(authHeader != null && authHeader.startsWith("Bearer ")){
         authHeader = authHeader.substring(7);
       }
       else {
         return onError(exchange, HttpStatus.UNAUTHORIZED, "Invalid format");
       }
       try {
        jwtUtil.validateToken(authHeader);
       }
       catch (Exception e){
        return onError(exchange, HttpStatus.UNAUTHORIZED, "Access denied");
       }
      }
      return chain.filter(exchange);
    });
  }

  private Mono<Void> onError(ServerWebExchange serverWebExchange, HttpStatus httpStatus, String string){
    ServerHttpResponse response = serverWebExchange.getResponse();
    response.setStatusCode(httpStatus);
    return response.setComplete();
  }

  public static class Config{}

}
