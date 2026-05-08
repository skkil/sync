package com.skkil.sync.like.dto.request;

import com.skkil.sync.like.enums.LikeTargetType;
import jakarta.validation.constraints.NotNull;

public record CreateLikeRequest(@NotNull LikeTargetType targetType, @NotNull Long targetId) {}
