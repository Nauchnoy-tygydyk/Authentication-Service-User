package org.example.paymentservice.mapper;

import org.example.paymentservice.entity.User;
import org.example.paymentservice.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IUserMapper {
  UserDto toDto(User user);
  User toEntity(UserDto userDto);
}