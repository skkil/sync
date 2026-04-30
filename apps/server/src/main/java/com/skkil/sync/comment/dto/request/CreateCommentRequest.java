package com.skkil.sync.comment.dto.request;

import com.skkil.sync.comment.enums.CommentTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
    @NotNull CommentTargetType targetType,
    @NotNull Long targetId,
    Long parentId,
    @NotBlank @Size(max = 10000) String content) {
}
