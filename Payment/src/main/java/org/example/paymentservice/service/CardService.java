package org.example.paymentservice.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.paymentservice.entity.PaymentCard;
import org.example.paymentservice.exception.LimitExceededException;
import org.example.paymentservice.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.example.paymentservice.repository.IUserRepository;
import org.example.paymentservice.repository.ICardRepository;


import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CardService {
private final ICardRepository cardRepository;
  private final IUserRepository userRepository;

@Transactional
public PaymentCard create(PaymentCard card) {
  if (card.getUser() == null || card.getUser().getId() == null) {
    log.error("Attempt to create a card without a user");
    throw new ResourceNotFoundException("User must be specified for the card");
  }

  Long userId = card.getUser().getId();

  log.info("Request to create a new card for user ID: {}", userId);

  if (!userRepository.existsById(userId)) {
    log.error("User with ID: {} not found. Card creation aborted.", userId);
    throw new ResourceNotFoundException("Can not create card, user with ID " + userId + " not found");
  }

  long count = cardRepository.countByUserId(userId);
  if (count >= 5) {
    log.warn("Limit exceeded for user ID: {}. Current count: {}", userId, count);
    throw new LimitExceededException("User already has " + count + " cards. Limit is 5.");
  }

  PaymentCard savedCard = cardRepository.save(card);
  log.info("Card successfully created with ID: {} for user ID: {}", savedCard.getId(), userId);

  return savedCard;
}

  @Transactional
  public void deactivateAllCardsByUserId(Long userId) {
    log.info("Deactivating all cards for user ID: {}", userId);
    cardRepository.deactivateAllCardsByUserId(userId);
  }


public PaymentCard getCardById(Long id) {
  return cardRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Card with current id:" + id + "was not found"));
}

@Transactional
  public PaymentCard update(Long id, PaymentCard cardDetails){
    PaymentCard existingCard = getCardById(id);
    existingCard.setNumber(cardDetails.getNumber());
    existingCard.setHolder(cardDetails.getHolder());
    existingCard.setExpirationDate(cardDetails.getExpirationDate());
    return cardRepository.save(existingCard);
  }

  public List<PaymentCard> getAllCardsByUserId(Long userId){
            return cardRepository.findByUserId(userId);
  }

  @Transactional
  public PaymentCard changeStatus(Long id, Boolean status) {
    PaymentCard existingCard = getCardById(id);
    existingCard.setActive(status);
    return cardRepository.save(existingCard);
  }

  public Page<PaymentCard> getAllCards(String holder, String number, Pageable pageable){
    Specification<PaymentCard> spec = Specification.where(null);
    if (holder != null && !holder.isEmpty()){
      spec = spec.and((root, query, criteriaBuilder) ->
              criteriaBuilder.like(root.get(PaymentCard.Fields.holder), "%" + holder + "%"));
    }
    if (number != null && !number.isEmpty()){
      spec = spec.and((root, query, criteriaBuilder) ->
              criteriaBuilder.like(root.get(PaymentCard.Fields.number), "%" + number + "%"));
    }
    return cardRepository.findAll(spec, pageable);
  }

  @Transactional
  public void delete(Long id) {
    log.warn("Deleting card with ID: {}", id);
    if (!cardRepository.existsById(id)) {
      throw new ResourceNotFoundException("Card not found with id: " + id);
    }
    cardRepository.deleteById(id);
  }
}
