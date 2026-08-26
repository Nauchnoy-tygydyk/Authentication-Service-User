package org.example.paymentservice.mapper;

import javax.annotation.processing.Generated;
import org.example.paymentservice.dto.PaymentCardDto;
import org.example.paymentservice.entity.PaymentCard;
import org.example.paymentservice.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-24T12:25:10+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class IPaymentCardMapperImpl implements IPaymentCardMapper {

    @Override
    public PaymentCardDto toDto(PaymentCard card) {
        if ( card == null ) {
            return null;
        }

        PaymentCardDto paymentCardDto = new PaymentCardDto();

        paymentCardDto.setUserId( cardUserId( card ) );
        paymentCardDto.setNumber( card.getNumber() );
        paymentCardDto.setHolder( card.getHolder() );
        paymentCardDto.setExpirationDate( card.getExpirationDate() );
        paymentCardDto.setActive( card.getActive() );

        return paymentCardDto;
    }

    @Override
    public PaymentCard toEntity(PaymentCardDto paymentCardDto) {
        if ( paymentCardDto == null ) {
            return null;
        }

        PaymentCard paymentCard = new PaymentCard();

        paymentCard.setUser( paymentCardDtoToUser( paymentCardDto ) );
        paymentCard.setNumber( paymentCardDto.getNumber() );
        paymentCard.setHolder( paymentCardDto.getHolder() );
        paymentCard.setExpirationDate( paymentCardDto.getExpirationDate() );
        paymentCard.setActive( paymentCardDto.getActive() );

        return paymentCard;
    }

    private Long cardUserId(PaymentCard paymentCard) {
        User user = paymentCard.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    protected User paymentCardDtoToUser(PaymentCardDto paymentCardDto) {
        if ( paymentCardDto == null ) {
            return null;
        }

        User user = new User();

        user.setId( paymentCardDto.getUserId() );

        return user;
    }
}
