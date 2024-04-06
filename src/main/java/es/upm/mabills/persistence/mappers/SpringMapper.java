package es.upm.mabills.persistence.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public @interface SpringMapper {
}
