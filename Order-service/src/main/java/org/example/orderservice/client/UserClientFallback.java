package org.example.orderservice.client;

import org.example.orderservice.dto.UserDto;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements IUserClient{

  @Override
  public UserDto getUserByEmail(String email){
    return new UserDto(-1L, "N/A", "N/A", null, email, false);
  }
}
