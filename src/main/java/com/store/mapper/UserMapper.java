package com.store.mapper;

import com.store.dto.UserDTO;
import com.store.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDTO toDTO(User user);

    List<UserDTO> toDTOList(List<User> users);

    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "accountNonExpired", ignore = true)
    @Mapping(target = "accountNonLocked", ignore = true)
    @Mapping(target = "credentialsNonExpired", ignore = true)
    @Mapping(target = "orders", ignore = true)
    User toEntity(UserDTO userDTO);

    @Mapping(target = "password", ignore = true)
    void updateEntityFromDTO(UserDTO userDTO, @MappingTarget User user);
}