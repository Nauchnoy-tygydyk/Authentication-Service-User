package org.example.orderservice.client;

import org.example.orderservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@FeignClient(
        name = "user-service",
        url = "${USER_SERVICE_URL:http://localhost:8083}",
        fallback = UserClientFallback.class
)
public interface IUserClient {

  @GetMapping("/api/users/email/{email}")
  UserDto getUserByEmail(@PathVariable("email") String email);

  @GetMapping("/api/users/{id}")
  UserDto getUserById(@PathVariable("id") Long id);

  @GetMapping("/api/users/batch")
  List<UserDto> getUsersByIds(@RequestParam("ids") List<Long> ids);
}