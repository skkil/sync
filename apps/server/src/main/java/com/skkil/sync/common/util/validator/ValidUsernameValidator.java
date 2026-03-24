package com.skkil.sync.common.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {

  private static final String USERNAME_PATTERN = "^[a-zA-Z0-9._-]+$";

  private int min;
  private int max;

  @Override
  public void initialize(ValidUsername constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);

    this.min = constraintAnnotation.min();
    this.max = constraintAnnotation.max();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true;
    }

    if (!value.matches(USERNAME_PATTERN)) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              "Username can only contain letters, numbers, dots, underscores, and hyphens")
          .addConstraintViolation();

      return false;
    }

    if (value.length() < min || value.length() > max) {
      context.disableDefaultConstraintViolation();
      context
          .buildConstraintViolationWithTemplate(
              String.format("Username must be between %d and %d characters", min, max))
          .addConstraintViolation();

      return false;
    }

    return true;
  }
}
