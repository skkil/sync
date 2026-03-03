package com.skkil.sync.common.util.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.net.URI;
import org.springframework.stereotype.Component;

@Component
public class ValidUrlValidator implements ConstraintValidator<ValidUrl, String> {

  public ValidUrlValidator() {}

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true;
    }

    try {
      URI uri = new URI(value);
      if (uri.getScheme() == null || uri.getHost() == null) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("URL must have a valid scheme and host")
            .addConstraintViolation();

        return false;
      }

      if (!uri.getScheme().equals("https")) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate("URL must use HTTPS scheme")
            .addConstraintViolation();

        return false;
      }

      return true;
    } catch (Exception e) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Invalid URL format").addConstraintViolation();
      return false;
    }
  }
}
