package com.skkil.sync.post.dto.data;

import com.skkil.sync.post.model.PostScope;
import com.skkil.sync.post.model.PostType;
import org.jspecify.annotations.Nullable;

public record PostRecommendationContext(
    Long requesterId, @Nullable PostScope scope, @Nullable PostType postType) {}
