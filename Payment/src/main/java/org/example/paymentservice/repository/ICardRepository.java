package org.example.paymentservice.repository;

import org.example.paymentservice.entity.PaymentCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface ICardRepository extends JpaRepository<PaymentCard, Long>, JpaSpecificationExecutor<PaymentCard> {

  List<PaymentCard> findByUserId(Long userId);

  @Query("SELECT count(c) FROM PaymentCard c WHERE c.user.id = :userId")
  long countByUserId(@Param("userId") Long userId);

  @Transactional
  @Modifying
  @Query(value = "UPDATE payment_cards SET active = false WHERE user_id = :userId", nativeQuery = true)
  void deactivateAllCardsByUserId(@Param("userId") Long userId);
}