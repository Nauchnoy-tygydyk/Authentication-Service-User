package org.example.authenticationservice.service;

import lombok.RequiredArgsConstructor;
import org.example.authenticationservice.client.IUserClient;
import org.example.authenticationservice.dto.AuthRequest;
import org.example.authenticationservice.dto.AuthResponse;
import org.example.authenticationservice.dto.UserDto;
import org.example.authenticationservice.dto.UserResponse;
import org.example.authenticationservice.entity.Role;
import org.example.authenticationservice.entity.UserCredentials;
import org.example.authenticationservice.exception.BadCredentialsException;
import org.example.authenticationservice.exception.UserAlreadyExistsException;
import org.example.authenticationservice.repository.IUserCredentialsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JwtService jwtService;
  private final IUserCredentialsRepository iUserCredentialsRepository;
  private final PasswordEncoder passwordEncoder;
  private final IUserClient userClient;

  @Transactional
  public UserResponse saveUser(AuthRequest request) {
    if (iUserCredentialsRepository.findByUsername(request.getUsername()).isPresent()) {
      throw new UserAlreadyExistsException("User with username " + request.getUsername() + " already exists");
    }

    UserCredentials credentials = new UserCredentials();
    credentials.setUserId(request.getUserId());
    credentials.setUsername(request.getUsername());
    credentials.setPassword(passwordEncoder.encode(request.getPassword()));
    credentials.setRole(Role.USER);

    UserCredentials saved = iUserCredentialsRepository.save(credentials);

    return new UserResponse(
            saved.getId(),
            saved.getUsername(),
            saved.getUserId()
    );
  }

  public AuthResponse login(AuthRequest authRequest) {
    UserCredentials user = iUserCredentialsRepository.findByUsername(authRequest.getUsername())
            .orElseThrow(() -> new BadCredentialsException("Wrong username or password"));

    if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
      throw new BadCredentialsException("Wrong username or password");
    }

    UserDto userDto = userClient.getUserByEmail(user.getUsername());
    if (userDto == null || !userDto.getActive()) {
      throw new BadCredentialsException("User account is disabled or not found");
    }

    String accessToken = jwtService.generateAccessToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    return new AuthResponse(accessToken, refreshToken);
  }

  public AuthResponse refreshToken(String refreshToken) {
    if (jwtService.validateToken(refreshToken)) {
      String username = jwtService.extractUsername(refreshToken);

      UserCredentials user = iUserCredentialsRepository.findByUsername(username)
              .orElseThrow(() -> new BadCredentialsException("User associated with token not found"));

      UserDto userDto = userClient.getUserByEmail(username);
      if (userDto == null || !userDto.getActive()) {
        throw new BadCredentialsException("User account is disabled or not found");
      }

      String newAccessToken = jwtService.generateAccessToken(user);
      String newRefreshToken = jwtService.generateRefreshToken(user);

      return new AuthResponse(newAccessToken, newRefreshToken);
    }
    throw new BadCredentialsException("Refresh token is expired or not valid");
  }

@Transactional
  public void deleteUser(String username){
    iUserCredentialsRepository.deleteByUsername(username);
  }
}