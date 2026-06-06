package com.skkil.sync.notification.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.dto.response.GetNotificationsResponse;
import com.skkil.sync.notification.service.NotificationService;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping("/notifications")
  @ResponseStatus(HttpStatus.OK)
  public GetNotificationsResponse getNotifications(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(required = false) Long cursor,
      @RequestParam(required = false) @Nullable NotificationStatus status) {
    return notificationService.getNotifications(user.userId(), size, cursor, status);
  }

  @PatchMapping("/notifications/read-all")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markAllNotificationsAsRead(@AuthenticationPrincipal AuthenticatedUser user) {
    notificationService.markAllAsRead(user.userId());
  }

  @PatchMapping("/notifications/{notificationId}/read")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void markNotificationAsRead(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long notificationId) {
    notificationService.markAsRead(user.userId(), notificationId);
  }
}
