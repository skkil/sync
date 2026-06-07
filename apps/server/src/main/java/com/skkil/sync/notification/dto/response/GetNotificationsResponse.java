package com.skkil.sync.notification.dto.response;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.constant.NotificationType;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record GetNotificationsResponse(CursorPaginationResponse<Notification> notifications) {

  public static record Notification(
      Long id,
      NotificationType type,
      NotificationStatus status,
      Instant createdAt,
      @Nullable Actor actor) {}

  public static record Actor(Long id, @Nullable String handle, String name) {}
}
