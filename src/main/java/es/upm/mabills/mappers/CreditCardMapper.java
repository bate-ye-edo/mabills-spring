package es.upm.mabills.mappers;

import es.upm.mabills.model.CreditCard;
import es.upm.mabills.persistence.entities.CreditCardEntity;
import org.mapstruct.Mapper;

@Mapper(uses = { UserMapper.class })
public interface CreditCardMapper {
    CreditCard toCreditCard(CreditCardEntity creditCardEntity);
}
