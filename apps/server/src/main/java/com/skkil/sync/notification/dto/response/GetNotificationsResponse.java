package com.skkil.sync.notification.dto.response;

import org.springframework.data.domain.Page;

public record GetNotificationsResponse(Page<Notification> notifications) {

  public static record Notification(Long id) {}
}
