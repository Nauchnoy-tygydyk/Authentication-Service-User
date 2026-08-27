package org.example.apigateway.config;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Predicate;

@Component
public class RouteValidator {
  private static final List<String> publicEndpoints = List.of(

          "/auth/save",
          "/auth/token"
  );

  public Predicate<ServerHttpRequest> isSecured = request ->
          publicEndpoints.stream()
                  .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
