package com.skkil.sync.project.validator;

import com.skkil.sync.project.constants.ProjectConstants;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class NotReservedHandleValidator implements ConstraintValidator<NotReservedHandle, String> {

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isEmpty()) {
      return true;
    }

    return !ProjectConstants.RESERVED_HANDLES.contains(value.toLowerCase(Locale.ROOT));
  }
}
