package org.example.paymentservice.integration;

import org.example.paymentservice.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserFlowIntegrationTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Test
  void fullFlowTest() {
    UserDto userDto = new UserDto();
    userDto.setName("Ivan");
    userDto.setSurname("Ivanov");
    String testEmail = "test_" + System.currentTimeMillis() + "@example.com";
    userDto.setEmail(testEmail);
    userDto.setBirthDate(LocalDate.of(1995, 5, 10));
    userDto.setActive(true);

    ResponseEntity<UserDto> response = restTemplate.postForEntity("/api/users", userDto, UserDto.class);
    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());

    ResponseEntity<UserDto> getResponse = restTemplate.getForEntity("/api/users/email/" + testEmail, UserDto.class);
    assertEquals(HttpStatus.OK, getResponse.getStatusCode());
  }
}