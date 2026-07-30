package org.example.paymentservice.repository;

import org.springframework.data.repository.query.Param;
import org.example.paymentservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends  JpaRepository<User, Long>,   JpaSpecificationExecutor<User>{

public List<User> findByNameAndSurname(String name, String surname);

  @Query("SELECT e FROM User e WHERE e.email = :email")
  List<User> findByEmail(@Param("email") String email);

  @Query(value = "SELECT count(*) FROM users", nativeQuery = true)
  long countAllUsersNative();
}
