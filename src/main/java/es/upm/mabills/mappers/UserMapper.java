package es.upm.mabills.mappers;

import es.upm.mabills.api.dtos.RegisterDto;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    @Named("toUser")
    User toUser(UserEntity userEntity);

    @Mapping(target = "bankAccounts", ignore = true)
    User toUser(RegisterDto registerDto);

    @Mapping(target = "password", ignore = true)
    User toUserProfile(UserEntity userEntity);
}
