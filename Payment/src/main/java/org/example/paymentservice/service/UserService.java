package org.example.paymentservice.service;

import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.exception.EmailAlreadyExistsException;
import org.example.paymentservice.exception.ResourceNotFoundException;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.example.paymentservice.repository.IUserRepository;
import org.example.paymentservice.entity.User;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class  UserService {

  private final IUserRepository userRepository;

  private User findUserOrThrow(Long id) {
    return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User with current id: " + id + " was not found"));
  }

  @Transactional
  public User create(User user) {
    log.info("Registering new user with email: {}", user.getEmail());
    String email = user.getEmail();
    if (userRepository.findByEmail(user.getEmail()).isEmpty()) {
      return userRepository.save(user);
    } else {
      throw new EmailAlreadyExistsException("User with current email: " + email + " already exists");
    }
  }

  @Cacheable(key = "#id")
  public User getUserById(Long id) {
    log.debug("Fetching user for cache or from DB, ID: {}", id);
    User user = findUserOrThrow(id);
    if (user.getCards() != null) {
      user.getCards().size();
    }
    return user;
  }

  public List<User> getUsersByNameAndSurname(String name, String surname) {
    return userRepository.findByNameAndSurname(name, surname);
  }

  public long getTotalUsersCount() {
    return userRepository.countAllUsersNative();
  }

  public List<User> getUserByEmail(String email) {
    List<User> users = userRepository.findByEmail(email);
    if (users.isEmpty()) {
      throw new ResourceNotFoundException("Пользователь с такой почтой не найден");
    }
    return users;
  }

  @CachePut(key = "#id")
  @Transactional
  public User update(Long id, User userDetails) {
    log.info("Updating user profile with ID: {}", id);

    User existingUser = findUserOrThrow(id);

    if (!existingUser.getEmail().equals(userDetails.getEmail())) {
      if (!userRepository.findByEmail(userDetails.getEmail()).isEmpty()) {
        throw new EmailAlreadyExistsException("Этот email уже занят другим пользователем");
      }
    }
    existingUser.setName(userDetails.getName());
    existingUser.setSurname(userDetails.getSurname());
    existingUser.setEmail(userDetails.getEmail());
    existingUser.setBirthDate(userDetails.getBirthDate());

    return userRepository.save(existingUser);
  }

  @CachePut(key = "#id")
  @Transactional
  public User changeStatus(Long id, Boolean status) {
    log.info("Changing status for user ID: {}", id);

    User existingUser = findUserOrThrow(id);

    existingUser.setActive(status);
    return userRepository.save(existingUser);
  }

  public Page<User> getAllUsers(String name, String surname, Pageable pageable) {
    Specification<User> spec = Specification.where(null);
    if (name != null && !name.isEmpty()) {
      spec = spec.and((root, query, criteriaBuilder) ->
              criteriaBuilder.like(root.get(User.Fields.name), "%" + name + "%"));
    }
    if (surname != null && !surname.isEmpty()) {
      spec = spec.and((root, query, criteriaBuilder) ->
              criteriaBuilder.like(root.get(User.Fields.surname), "%" + surname + "%"));
    }
    return userRepository.findAll(spec, pageable);
  }

  @CacheEvict(key = "#id")
  @Transactional
  public void delete(Long id) {
    log.warn("Executing deletion of user ID: {}", id);
    if (!userRepository.existsById(id)) {
      throw new ResourceNotFoundException("User not found with id: " + id);
    }
    userRepository.deleteById(id);
  }
}