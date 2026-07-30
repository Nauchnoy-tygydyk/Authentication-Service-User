package org.example.paymentservice.Service;

import org.example.paymentservice.entity.User;
import org.example.paymentservice.repository.IUserRepository;
import org.example.paymentservice.exception.EmailAlreadyExistsException;
import org.example.paymentservice.exception.ResourceNotFoundException;
import org.example.paymentservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @Mock
  private IUserRepository userRepository;

  @InjectMocks
  private UserService userService;

  @Test
  void create_ShouldSaveUser_WhenEmailIsUnique() {
    User user = new User();
    user.setEmail("new@test.com");
    when(userRepository.findByEmail("new@test.com")).thenReturn(Collections.emptyList());
    when(userRepository.save(any(User.class))).thenReturn(user);

    User result = userService.create(user);

    assertNotNull(result);
    verify(userRepository).save(user);
  }

  @Test
  void create_ShouldThrowException_WhenEmailAlreadyExists() {
    User user = new User();
    user.setEmail("exists@test.com");
    when(userRepository.findByEmail("exists@test.com")).thenReturn(List.of(new User()));

    assertThrows(EmailAlreadyExistsException.class, () -> userService.create(user));
    verify(userRepository, never()).save(any());
  }

  @Test
  void getUserById_ShouldThrowException_WhenNotFound() {
    when(userRepository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
  }
}