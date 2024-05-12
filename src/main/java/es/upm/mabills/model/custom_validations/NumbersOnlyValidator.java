package es.upm.mabills.model.custom_validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NumbersOnlyValidator implements ConstraintValidator<NumbersOnly, String> {
    private static final String NUMBERS_ONLY_REGEX = "^\\d*$";

    @Override
    public void initialize(NumbersOnly constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        return s.matches(NUMBERS_ONLY_REGEX);
    }
}
