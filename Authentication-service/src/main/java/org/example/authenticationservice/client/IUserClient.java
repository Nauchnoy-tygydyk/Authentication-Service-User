package org.example.authenticationservice.client;

import org.example.authenticationservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${app.services.user-service.url}")
public interface IUserClient {

  @GetMapping("/api/users/email/{email}")
  UserDto getUserByEmail(@PathVariable("email") String email);
}