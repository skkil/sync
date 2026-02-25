package com.skkil.sync.common.util.validator;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@Import(ValidUsernameValidator.class)
public class ValidUsernameValidatorTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static record TestDto(@ValidUsername String username) {}

  @Test
  void validUsernameValidator_whenValidUsername_thenNoConstraintViolation() {
    TestDto dto = new TestDto("valid_username");
    assertThat(validator.validate(dto)).isEmpty();
  }

  @Test
  void validUsernameValidator_whenUsernameContainsInvalidCharacters_thenConstraintViolation() {
    TestDto dto = new TestDto("invalid-usern'ame!'");
    assertThat(validator.validate(dto)).isNotEmpty();
  }
}
