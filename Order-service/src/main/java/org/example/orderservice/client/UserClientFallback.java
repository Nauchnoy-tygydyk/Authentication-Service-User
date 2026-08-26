package org.example.orderservice.client;

import org.example.orderservice.dto.UserDto;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class UserClientFallback implements IUserClient {

  @Override
  public UserDto getUserByEmail(String email) {
    return new UserDto(-1L, "N/A", "N/A", null, email, false);
  }

  @Override
  public UserDto getUserById(Long id) {
    return new UserDto(id, "N/A", "N/A", null, "N/A", false);
  }

  @Override
  public List<UserDto> getUsersByIds(List<Long> ids) {
    return Collections.emptyList();
  }
}