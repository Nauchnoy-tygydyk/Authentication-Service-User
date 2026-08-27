package org.example.apigateway.service;

import org.example.apigateway.dto.RegistrationRequestDto;
import org.example.apigateway.dto.AuthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationService {

  private final WebClient authWebClient;
  private final WebClient userWebClient;

  public RegistrationService(
          WebClient.Builder builder,
          @Value("${services.auth-service.url}") String authUrl,
          @Value("${services.user-service.url}") String userUrl
  ) {

    this.authWebClient = builder.baseUrl(authUrl).build();
    this.userWebClient = builder.baseUrl(userUrl).build();
  }


  public Mono<Map> saveCredentials(RegistrationRequestDto registrationRequestDto){

    AuthRequest request = new AuthRequest();
    request.setPassword(registrationRequestDto.getPassword());
    request.setUsername(registrationRequestDto.getUsername());

    return authWebClient
            .post()
            .uri("/auth/save")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Map.class);
  }

  public Mono<Void> saveUser(RegistrationRequestDto registrationRequestDto, Object userId){
    Map<String, Object> body = new HashMap<>();
    body.put("id",userId);
    body.put("name", registrationRequestDto.getName());
    body.put("surname", registrationRequestDto.getSurname());
    body.put("email", registrationRequestDto.getEmail());
    body.put("active", true);

    return userWebClient
            .post()
            .uri("/api/users")
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Void.class);
  }


  private Mono<Void> rollbackAuth(String username){
    return authWebClient
            .delete()
            .uri("/auth/delete/" + username)
            .retrieve()
            .bodyToMono(Void.class);
  }

  public Mono<ResponseEntity<Object>> registerUser(RegistrationRequestDto registrationRequestDto) {
    return saveCredentials(registrationRequestDto)
            .flatMap(authResponse -> {
              Object userId = authResponse.get("userId");

              return saveUser(registrationRequestDto, userId)
                      .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
            })
            .onErrorResume(error -> {
              return rollbackAuth(registrationRequestDto.getUsername())
                      .then(Mono.just(ResponseEntity
                              .status(HttpStatus.INTERNAL_SERVER_ERROR)
                              .body( (Object) "Registration failed. Changes have been rolled back.")));
            });
  }
}