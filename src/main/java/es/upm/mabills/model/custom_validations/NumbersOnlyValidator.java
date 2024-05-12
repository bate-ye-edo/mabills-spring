package es.upm.mabills.model.custom_validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class NumbersOnlyValidator implements ConstraintValidator<NumbersOnly, String> {
    private static final Pattern NUMBERS_ONLY_REGEX = Pattern.compile("^\\d*$");

    @Override
    public void initialize(NumbersOnly constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        return NUMBERS_ONLY_REGEX.asMatchPredicate().test(s);
    }
}
