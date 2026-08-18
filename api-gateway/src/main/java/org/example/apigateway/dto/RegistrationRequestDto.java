package org.example.apigateway.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequestDto {

  @NotBlank
  private String username;

  @NotBlank
  @Size(min = 3, max = 128)
  private String password;

  @NotBlank
  private String name;

  @NotBlank
  private String surname;

  @NotBlank
  @Email
  private String email;
}
