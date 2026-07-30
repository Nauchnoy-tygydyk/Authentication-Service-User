package org.example.paymentservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.paymentservice.entity.PaymentCard;
import org.example.paymentservice.service.CardService;
import org.example.paymentservice.dto.PaymentCardDto;
import org.example.paymentservice.mapper.IPaymentCardMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RequestMapping("/api/cards")
@RestController

public class CardController {
  private final CardService cardService;
  private final IPaymentCardMapper iPaymentCardMapper;

  @PostMapping
  public ResponseEntity<PaymentCardDto> createCard(@Valid @RequestBody PaymentCardDto cardDto) {
    PaymentCard entity = iPaymentCardMapper.toEntity(cardDto);
    PaymentCard savedCards = cardService.create(entity);
    PaymentCardDto resultDto = iPaymentCardMapper.toDto(savedCards);
    return ResponseEntity.status(HttpStatus.CREATED).body(resultDto);


  }

  @PatchMapping("/user/{userId}/deactivate-all")
  public ResponseEntity<Void> deactivateAll(@PathVariable Long userId) {
    cardService.deactivateAllCardsByUserId(userId);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentCardDto> getCardById(@PathVariable Long id) {
    PaymentCard cardEntity = cardService.getCardById(id);
    PaymentCardDto resultDto = iPaymentCardMapper.toDto(cardEntity);
    return ResponseEntity.ok(resultDto);
  }

  @PutMapping("/{id}")
  public ResponseEntity<PaymentCardDto> updateCard(@PathVariable Long id, @Valid @RequestBody PaymentCardDto paymentCardDto) {
    PaymentCard entity = iPaymentCardMapper.toEntity(paymentCardDto);
    PaymentCard updatedCards = cardService.update(id, entity);
    PaymentCardDto resultDto = iPaymentCardMapper.toDto(updatedCards);
    return ResponseEntity.ok(resultDto);
  }
  @PatchMapping("/{id}/status")
  public ResponseEntity<PaymentCardDto> changeStatus(@PathVariable Long id, @RequestParam Boolean status) {
    PaymentCard entity = cardService.changeStatus(id, status);
    PaymentCardDto resultDto = iPaymentCardMapper.toDto(entity);
    return ResponseEntity.ok(resultDto);
  }

  @GetMapping()
  public ResponseEntity<Page<PaymentCardDto>> getAllCards(@RequestParam(required = false) String holder, @RequestParam(required = false) String number, Pageable pageable){
    Page<PaymentCard> cards = cardService.getAllCards(holder, number, pageable);
    return  ResponseEntity.ok(cards.map(iPaymentCardMapper::toDto));
  }

  @GetMapping("/user/{userId}")
  public ResponseEntity<List<PaymentCardDto>> getCardByUserId(@PathVariable Long userId){
    List<PaymentCard> cards = cardService.getAllCardsByUserId(userId);
    return ResponseEntity.ok(cards.stream().map(iPaymentCardMapper::toDto).collect(Collectors.toList()));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
    cardService.delete(id);
    return ResponseEntity.noContent().build();
  }
}

