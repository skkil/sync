package com.skkil.sync.post.validator;

import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

@Component
public class ValidPublishablePostValidator
    implements ConstraintValidator<
        ValidPublishablePost, ValidPublishablePostValidator.PublishablePostRequest> {

  @Override
  public boolean isValid(PublishablePostRequest value, ConstraintValidatorContext context) {
    if (value == null || (value.status() != null && value.status() != PostStatus.PUBLISHED)) {
      return true;
    }

    return value.type() == PostType.SHORT || (value.title() != null && !value.title().isBlank());
  }

  public interface PublishablePostRequest {

    String title();

    PostType type();

    PostStatus status();
  }
}
