package org.example.authenticationservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.dto.AuthRequest;
import org.example.authenticationservice.dto.AuthResponse;
import org.example.authenticationservice.dto.UserResponse;
import org.example.authenticationservice.service.AuthService;
import org.example.authenticationservice.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Valid
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;
  private final JwtService jwtService;

  @PostMapping("/save")
  public ResponseEntity<UserResponse> endpointRegistration(@Valid @RequestBody AuthRequest authRequest) {
    UserResponse response = authService.saveUser(authRequest);

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/token")
  public ResponseEntity<AuthResponse> endpointLogin(@Valid @RequestBody AuthRequest authRequest) {
    AuthResponse response = authService.login(authRequest);
    return ResponseEntity.ok(response);
  }


  @GetMapping("/validate")
  public Boolean endpointValidation(@RequestParam String token){
    return jwtService.validateToken(token);
  }

  @PostMapping("/refresh")
  public AuthResponse endpointRefresh(@RequestParam String refreshToken) {
    return authService.refreshToken(refreshToken);
  }

  @DeleteMapping("/delete/{username}")
  public ResponseEntity<Void> endpointDelete(@PathVariable String username) {
    authService.deleteUser(username);
    return ResponseEntity.noContent().build();
  }

}
