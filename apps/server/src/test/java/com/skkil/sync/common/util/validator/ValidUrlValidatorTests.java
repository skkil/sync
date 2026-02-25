package com.skkil.sync.common.util.validator;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@Import(ValidUrlValidator.class)
public class ValidUrlValidatorTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static record TestDto(@ValidUrl String url) {}

  @Test
  void validUrlValidator_whenNullUrl_thenNoConstraintViolation() {
    TestDto dto = new TestDto(null);
    assertThat(validator.validate(dto)).isEmpty();
  }

  @Test
  void validUrlValidator_whenValidUrl_thenNoConstraintViolation() {
    TestDto dto = new TestDto("https://www.example.com");
    assertThat(validator.validate(dto)).isEmpty();
  }

  @Test
  void validUrlValidator_whenUrlWithoutProtocol_thenConstraintViolation() {
    TestDto dto = new TestDto("www.example.com");
    assertThat(validator.validate(dto)).isNotEmpty();
  }

  @Test
  void validUrlValidator_whenProtocolIsNotHttps_thenConstraintViolation() {
    TestDto dto = new TestDto("http://www.example.com");
    assertThat(validator.validate(dto)).isNotEmpty();
  }
}
