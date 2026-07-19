package com.skkil.sync.post.dto.request;

import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.validator.ValidPublishablePost;
import com.skkil.sync.post.validator.ValidPublishablePostValidator.PublishablePostRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@ValidPublishablePost
public record UpdatePostRequest(
    String title,
    @NotNull PostType type,
    @NotNull PostStatus status,
    @Valid @NotNull PostContentRequest content,
    List<String> tags)
    implements PublishablePostRequest {}
