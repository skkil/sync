package com.skkil.sync.notification.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.notification.dto.response.GetNotificationsResponse;
import com.skkil.sync.notification.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @GetMapping("/notifications")
  public GetNotificationsResponse getNotifications(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam Long cursor) {
    return notificationService.getNotifications(user.userId(), size, cursor);
  }
}
