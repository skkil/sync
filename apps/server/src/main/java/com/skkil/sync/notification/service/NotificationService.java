package com.skkil.sync.notification.service;

import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.dto.response.GetNotificationsResponse;
import com.skkil.sync.notification.exception.NotificationNotFoundException;
import com.skkil.sync.notification.model.Notification;
import com.skkil.sync.notification.repository.NotificationRepository;
import com.skkil.sync.user.model.User;
import java.util.List;
import org.jspecify.annotations.Nullable;
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
  public GetNotificationsResponse getNotifications(
      Long userId, int size, Long cursor, @Nullable NotificationStatus status) {
    Pageable pageable = Pageable.ofSize(size);

    var page = notificationRepository.findByUser(userId, status, pageable, cursor);
    List<CursorPaginationResponse.Node<GetNotificationsResponse.Notification>> nodes =
        page.getContent().stream()
            .map(
                notification ->
                    new CursorPaginationResponse.Node<>(
                        String.valueOf(notification.getId()), toNotificationResponse(notification)))
            .toList();

    CursorPaginationResponse.PageInfo pageInfo =
        CursorPaginationResponse.PageInfo.builder()
            .size(nodes.size())
            .hasNextPage(page.hasNext())
            .hasPreviousPage(cursor != null)
            .startCursor(!nodes.isEmpty() ? nodes.get(0).cursor() : null)
            .endCursor(!nodes.isEmpty() ? nodes.get(nodes.size() - 1).cursor() : null)
            .build();

    return new GetNotificationsResponse(new CursorPaginationResponse<>(pageInfo, nodes));
  }

  @Transactional
  public void markAsRead(Long userId, Long notificationId) {
    Notification notification =
        notificationRepository
            .findByIdAndUser_Id(notificationId, userId)
            .orElseThrow(() -> new NotificationNotFoundException(notificationId));

    notification.markAsRead();
  }

  @Transactional
  public void markAllAsRead(Long userId) {
    notificationRepository.updateStatusByUser(
        userId, NotificationStatus.UNREAD, NotificationStatus.READ);
  }

  private GetNotificationsResponse.Notification toNotificationResponse(Notification notification) {
    return new GetNotificationsResponse.Notification(
        notification.getId(),
        notification.getType(),
        notification.getStatus(),
        notification.getCreatedAt(),
        toActorResponse(notification.getActor()));
  }

  private GetNotificationsResponse.Actor toActorResponse(@Nullable User actor) {
    if (actor == null) {
      return null;
    }

    return new GetNotificationsResponse.Actor(
        actor.getId(), actor.getHandle(), actor.getFullName());
  }
}
