package com.skkil.sync.notification.service;

import com.skkil.sync.notification.dto.response.GetNotificationsResponse;
import com.skkil.sync.notification.repository.NotificationRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;

  public NotificationService(NotificationRepository notificationRepository) {
    this.notificationRepository = notificationRepository;
  }

  @Transactional(readOnly = true)
  public GetNotificationsResponse getNotifications(Long userId, int size, Long cursor) {
    Pageable pageable = Pageable.ofSize(size);

    var notifications =
        notificationRepository
            .findByUser(userId, pageable, cursor)
            .map(notification -> new GetNotificationsResponse.Notification(notification.getId()));

    return new GetNotificationsResponse(notifications);
  }
}
