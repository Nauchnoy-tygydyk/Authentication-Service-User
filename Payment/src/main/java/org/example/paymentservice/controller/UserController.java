package org.example.paymentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.paymentservice.entity.User;
import org.example.paymentservice.service.UserService;
import org.example.paymentservice.dto.UserDto;
import org.example.paymentservice.mapper.IUserMapper;
import org.example.paymentservice.util.AccessValidator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/users")
@RestController
public class UserController {

  private final UserService userService;
  private final IUserMapper iUserMapper;
  private final AccessValidator accessValidator;

  @PostMapping
  public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
    User entity = iUserMapper.toEntity(userDto);
    User savedUsers = userService.create(entity);
    UserDto resultDto = iUserMapper.toDto(savedUsers);
    return ResponseEntity.status(HttpStatus.CREATED).body(resultDto);
  }

  @GetMapping("/count")
  public ResponseEntity<Long> countUsers(@RequestHeader("X-User-Role") String role) {
    if (!"ADMIN".equals(role)) {
      throw new RuntimeException("Access denied: Admin only");
    }
    return ResponseEntity.ok(userService.getTotalUsersCount());
  }

  @GetMapping("/search")
  public ResponseEntity<List<UserDto>> searchUsers(
          @RequestParam String name,
          @RequestParam String surname,
          @RequestHeader("X-User-Role") String role) {
    if (!"ADMIN".equals(role)) {
      throw new RuntimeException("Access denied: Admin only");
    }
    List<User> users = userService.getUsersByNameAndSurname(name, surname);
    return ResponseEntity.ok(users.stream().map(iUserMapper::toDto).toList());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDto> getUserById(
          @PathVariable Long id,
          @RequestHeader("X-User-Id") Long currentUserId,
          @RequestHeader("X-User-Role") String role) {
    accessValidator.validate(currentUserId, role, id);
    User userEntity = userService.getUserById(id);
    return ResponseEntity.ok(iUserMapper.toDto(userEntity));
  }

  @PutMapping("/{id}")
  public ResponseEntity<UserDto> updateUser(
          @PathVariable Long id,
          @RequestHeader("X-User-Id") Long currentUserId,
          @RequestHeader("X-User-Role") String role,
          @Valid @RequestBody UserDto userDto) {
    accessValidator.validate(currentUserId, role, id);
    User entity = iUserMapper.toEntity(userDto);
    User updatedUsers = userService.update(id, entity);
    return ResponseEntity.ok(iUserMapper.toDto(updatedUsers));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<UserDto> changeStatus(
          @PathVariable Long id,
          @RequestParam Boolean status,
          @RequestHeader("X-User-Role") String role) {
    if (!"ADMIN".equals(role)) {
      throw new RuntimeException("Access denied: Admin only");
    }
    User entity = userService.changeStatus(id, status);
    return ResponseEntity.ok(iUserMapper.toDto(entity));
  }

  @GetMapping()
  public ResponseEntity<Page<UserDto>> getAllPerson(
          @RequestParam(required = false) String name,
          @RequestParam(required = false) String surname,
          Pageable pageable,
          @RequestHeader("X-User-Role") String role) {
    if (!"ADMIN".equals(role)) {
      throw new RuntimeException("Access denied: Admin only");
    }
    Page<User> users = userService.getAllUsers(name, surname, pageable);
    return ResponseEntity.ok(users.map(iUserMapper::toDto));
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<UserDto> getUserByEmail(
          @PathVariable String email,
          @RequestHeader("X-User-Id") Long currentUserId,
          @RequestHeader("X-User-Role") String role) {
    List<User> users = userService.getUserByEmail(email);
    User foundUser = users.get(0);
    accessValidator.validate(currentUserId, role, foundUser.getId());
    return ResponseEntity.ok(iUserMapper.toDto(foundUser));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUser(
          @PathVariable Long id,
          @RequestHeader("X-User-Role") String role) {
    if (!"ADMIN".equals(role)) {
      throw new RuntimeException("Access denied: Admin only");
    }
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }
}