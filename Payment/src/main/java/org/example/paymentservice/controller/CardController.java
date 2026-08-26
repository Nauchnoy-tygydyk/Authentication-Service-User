package org.example.paymentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.paymentservice.entity.PaymentCard;
import org.example.paymentservice.service.CardService;
import org.example.paymentservice.dto.PaymentCardDto;
import org.example.paymentservice.mapper.IPaymentCardMapper;
import org.example.paymentservice.util.AccessValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardController {

  private final CardService cardService;
  private final IPaymentCardMapper icardMapper;
  private final AccessValidator accessValidator;

  @PostMapping
  public ResponseEntity<PaymentCardDto> createCard(
          @Valid @RequestBody PaymentCardDto cardDto,
          @RequestHeader("X-User-Id") Long currentUserId) {

    cardDto.setUserId(currentUserId);
    PaymentCard entity = icardMapper.toEntity(cardDto);
    PaymentCard saved = cardService.create(entity);
    return ResponseEntity.status(HttpStatus.CREATED).body(icardMapper.toDto(saved));
  }

  @PatchMapping("/user/{userId}/deactivate-all")
  public ResponseEntity<Void> deactivateAll(
          @PathVariable Long userId,
          @RequestHeader("X-User-Id") Long currentUserId,
          @RequestHeader("X-User-Role") String role) {

    accessValidator.validate(currentUserId, role, userId);
    cardService.deactivateAllCardsByUserId(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentCardDto> getCardById(
          @PathVariable Long id,
          @RequestHeader("X-User-Id") Long currentUserId,
          @RequestHeader("X-User-Role") String role) {

    PaymentCard card = cardService.getCardById(id);
    accessValidator.validate(currentUserId, role, card.getUser().getId());
    return ResponseEntity.ok(icardMapper.toDto(card));
  }

  @PutMapping("/{id}")
  public ResponseEntity<PaymentCardDto> updateCard(
          @PathVariable Long id,
          @Valid @RequestBody PaymentCardDto paymentCardDto,
          @RequestHeader("X-User-Id") Long currentUserId,
          @RequestHeader("X-User-Role") String role) {

    PaymentCard existingCard = cardService.getCardById(id);
    accessValidator.validate(currentUserId, role, existingCard.getUser().getId());

    PaymentCard entity = icardMapper.toEntity(paymentCardDto);
    PaymentCard updated = cardService.update(id, entity);
    return ResponseEntity.ok(icardMapper.toDto(updated));
  }

  @PatchMapping("/{id}/status")
  public ResponseEntity<PaymentCardDto> changeStatus(
          @PathVariable Long id,
          @RequestParam Boolean status,
          @RequestHeader("X-User-Role") String role) {

    if (!"ADMIN".equals(role)) {
      throw new RuntimeException("Access denied: Admin only");
    }
    PaymentCard updated = cardService.changeStatus(id, status);
    return ResponseEntity.ok(icardMapper.toDto(updated));
  }

  @GetMapping
  public ResponseEntity<Page<PaymentCardDto>> getAllCards(
          @RequestParam(required = false) String holder,
          @RequestParam(required = false) String number,
          @RequestHeader("X-User-Role") String role,
          Pageable pageable) {

    if (!"ADMIN".equals(role)) {
      throw new RuntimeException("Access denied: Admin only");
    }
    Page<PaymentCard> cards = cardService.getAllCards(holder, number, pageable);
    return ResponseEntity.ok(cards.map(icardMapper::toDto));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PaymentCardDto>> getCardByUserId(
          @PathVariable Long userId,
          @RequestHeader("X-User-Id") Long currentUserId,
          @RequestHeader("X-User-Role") String role) {

    accessValidator.validate(currentUserId, role, userId);
    List<PaymentCard> cards = cardService.getAllCardsByUserId(userId);
    return ResponseEntity.ok(cards.stream().map(icardMapper::toDto).toList());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCard(
          @PathVariable Long id,
          @RequestHeader("X-User-Id") Long currentUserId,
          @RequestHeader("X-User-Role") String role) {

    PaymentCard card = cardService.getCardById(id);
    accessValidator.validate(currentUserId, role, card.getUser().getId());
    cardService.delete(id);
    return ResponseEntity.noContent().build();
  }
}