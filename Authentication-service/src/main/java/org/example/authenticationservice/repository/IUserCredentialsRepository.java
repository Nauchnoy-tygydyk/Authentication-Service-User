package org.example.authenticationservice.repository;

import org.example.authenticationservice.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface IUserCredentialsRepository extends JpaRepository<UserCredentials, Long> {
  Optional<UserCredentials> findByUsername(String username);

  @Transactional
  void deleteByUsername(String username);
}