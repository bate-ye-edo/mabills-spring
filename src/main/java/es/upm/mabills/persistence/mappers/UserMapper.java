package es.upm.mabills.persistence.mappers;

import es.upm.mabills.model.User;
import es.upm.mabills.persistence.entities.UserEntity;

@SpringMapper
public interface UserMapper {
    User toUser(UserEntity userEntity);
}
