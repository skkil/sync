package com.skkil.sync.notification.model;

import org.jspecify.annotations.Nullable;

/** {@code postTitle}은 SHORT 게시글에서 null이다 — 제목은 그 타입에서만 선택이기 때문이다. */
public record NewLikePayload(
    String actorHandle, String actorName, @Nullable String postTitle, String postSlug)
    implements NotificationPayload {}
