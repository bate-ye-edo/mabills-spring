package es.upm.mabills.mappers;

import es.upm.mabills.api.dtos.RegisterDto;
import es.upm.mabills.model.User;
import es.upm.mabills.persistence.entities.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserMapper {
    @Mapping(target = "password", ignore = true)
    User toUser(UserEntity userEntity);
    User toUser(RegisterDto registerDto);
}
