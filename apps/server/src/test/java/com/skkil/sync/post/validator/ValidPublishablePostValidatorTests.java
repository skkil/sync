package com.skkil.sync.post.validator;

import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.post.dto.request.CreatePostRequest;
import com.skkil.sync.post.dto.request.PostContentRequest;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;

@Import(ValidPublishablePostValidator.class)
class ValidPublishablePostValidatorTests {

  private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  private static CreatePostRequest createPostRequest(
      PostType type, String title, PostStatus status) {
    return CreatePostRequest.builder()
        .title(title)
        .type(type)
        .status(status)
        .content(new PostContentRequest("content", "{\"text\":\"content\"}", List.of()))
        .tags(List.of("tag"))
        .build();
  }

  @Test
  void validPublishablePostValidator_whenPublishedQuestionWithoutTitle_thenConstraintViolation() {
    CreatePostRequest request = createPostRequest(PostType.QUESTION, " ", PostStatus.PUBLISHED);

    assertThat(validator.validate(request)).isNotEmpty();
  }

  @Test
  void validPublishablePostValidator_whenPublishedQuestionWithTitle_thenNoConstraintViolation() {
    CreatePostRequest request = createPostRequest(PostType.QUESTION, "제목", PostStatus.PUBLISHED);

    assertThat(validator.validate(request)).isEmpty();
  }

  @Test
  void validPublishablePostValidator_whenDraftWithoutTitle_thenNoConstraintViolation() {
    CreatePostRequest request = createPostRequest(PostType.QUESTION, "", PostStatus.DRAFT);

    assertThat(validator.validate(request)).isEmpty();
  }

  @Test
  void validPublishablePostValidator_whenPublishedShortWithoutTitle_thenNoConstraintViolation() {
    CreatePostRequest request = createPostRequest(PostType.SHORT, "", PostStatus.PUBLISHED);

    assertThat(validator.validate(request)).isEmpty();
  }
}
