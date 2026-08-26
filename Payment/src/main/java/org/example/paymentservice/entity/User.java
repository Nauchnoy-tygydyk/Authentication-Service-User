package org.example.paymentservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldNameConstants
@Entity
@Table(name = "users")
public class User extends Auditable implements Serializable {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
          private String name;

  @Column(name = "surname")
          private String surname;

  @Column(name = "birth_date")
          private LocalDate birthDate;

  @Column(name = "email", unique = true, nullable = false)
          private String email;

  @Column(name = "active")
          private Boolean active;


  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private List<PaymentCard> cards;
}
