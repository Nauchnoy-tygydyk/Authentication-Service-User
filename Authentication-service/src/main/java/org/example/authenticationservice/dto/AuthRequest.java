package org.example.authenticationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRequest {
  @NotBlank
  private String username;

  @NotBlank
  @Size(min = 3)
  private String password;
  private Long userId;
  private String role;
}
