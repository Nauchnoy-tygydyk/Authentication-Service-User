package org.example.paymentservice.integration;

import org.example.paymentservice.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Профиль "test" активирует H2 и выключает основной SecurityConfig
@Import(UserFlowIntegrationTest.SecurityBypassConfig.class)
public class UserFlowIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @TestConfiguration
  static class SecurityBypassConfig {
    @Bean
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
      http
              .csrf(AbstractHttpConfigurer::disable)
              .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
      return http.build();
    }
  }

  @Test
  void fullFlowTest() {
    UserDto userDto = new UserDto();
    userDto.setName("Ivan");
    userDto.setSurname("Ivanov");
    String testEmail = "test_" + System.currentTimeMillis() + "@example.com";
    userDto.setEmail(testEmail);
    userDto.setBirthDate(LocalDate.of(1995, 5, 10));
    userDto.setActive(true);

    // POST
    ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/users", userDto, UserDto.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());

    // GET
    ResponseEntity<UserDto> getResponse = restTemplate.getForEntity("/api/users/email/" + testEmail, UserDto.class);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }
}