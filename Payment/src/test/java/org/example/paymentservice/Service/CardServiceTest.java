package org.example.paymentservice.Service;

import org.example.paymentservice.entity.PaymentCard;
import org.example.paymentservice.entity.User;
import org.example.paymentservice.repository.ICardRepository;
import org.example.paymentservice.repository.IUserRepository;
import org.example.paymentservice.exception.LimitExceededException;
import org.example.paymentservice.service.CardService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

  @Mock
  private ICardRepository cardRepository;
  @Mock
  private IUserRepository userRepository;

  @InjectMocks
  private CardService cardService;

  @Test
  void create_ShouldThrowException_WhenUserHas5Cards() {
    User user = new User();
    user.setId(1L);
    PaymentCard newCard = new PaymentCard();
    newCard.setUser(user);

    // Имитируем, что карт уже 5
    when(cardRepository.countByUserId(1L)).thenReturn(5L);

    assertThrows(LimitExceededException.class, () -> cardService.create(newCard));
    verify(cardRepository, never()).save(any());
  }

  @Test
  void create_ShouldSave_WhenLimitNotReached() {
    User user = new User();
    user.setId(1L);
    PaymentCard newCard = new PaymentCard();
    newCard.setUser(user);

    when(cardRepository.countByUserId(1L)).thenReturn(2L);
    when(userRepository.existsById(1L)).thenReturn(true);
    when(cardRepository.save(newCard)).thenReturn(newCard);

    PaymentCard result = cardService.create(newCard);

    assertNotNull(result);
    verify(cardRepository).save(newCard);
  }
}