package org.example.apigateway.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.apigateway.dto.RegistrationRequestDto;
import org.example.apigateway.service.RegistrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class RegistrationController {

  private final RegistrationService registrationService;

  @PostMapping("/register")
  public Mono<ResponseEntity<Object>> register(@Valid @RequestBody RegistrationRequestDto registrationRequestDto) {

    return registrationService.registerUser(registrationRequestDto);
  }
}
