package es.upm.mabills.model.custom_validations;

import jakarta.validation.Constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

@Constraint(validatedBy = NumbersOnlyValidator.class)
@Target({FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface NumbersOnly {
    String message() default "Only numbers are allowed";
    Class<?>[] groups() default {};
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}
