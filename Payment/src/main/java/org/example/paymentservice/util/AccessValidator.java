package org.example.paymentservice.util;

import org.springframework.stereotype.Component;

@Component
public class AccessValidator {

  public void validate(Long currentUserId, String role, Long id){

    if("ADMIN".equals(role)){
      return;
    }

    if (!currentUserId.equals(id)){
      throw  new RuntimeException("You have no acces");
    }
  }

}
