package org.example.paymentservice.mapper;

import javax.annotation.processing.Generated;
import org.example.paymentservice.dto.UserDto;
import org.example.paymentservice.entity.User;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-08-24T12:25:10+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.11 (Microsoft)"
)
@Component
public class IUserMapperImpl implements IUserMapper {

    @Override
    public UserDto toDto(User user) {
        if ( user == null ) {
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setName( user.getName() );
        userDto.setSurname( user.getSurname() );
        userDto.setBirthDate( user.getBirthDate() );
        userDto.setEmail( user.getEmail() );
        userDto.setActive( user.getActive() );

        return userDto;
    }

    @Override
    public User toEntity(UserDto userDto) {
        if ( userDto == null ) {
            return null;
        }

        User user = new User();

        user.setName( userDto.getName() );
        user.setSurname( userDto.getSurname() );
        user.setBirthDate( userDto.getBirthDate() );
        user.setEmail( userDto.getEmail() );
        user.setActive( userDto.getActive() );

        return user;
    }
}
