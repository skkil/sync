package com.skkil.sync.notification.service;

import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.OffsetPaginationResponse;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.dto.data.NotificationSummary;
import com.skkil.sync.notification.dto.response.GetNotificationsResponse;
import com.skkil.sync.notification.exception.NotificationNotFoundException;
import com.skkil.sync.notification.mapper.NotificationMapper;
import com.skkil.sync.notification.model.Notification;
import com.skkil.sync.notification.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final NotificationMapper notificationMapper;
  private final MediaDomainService mediaDomainService;
  private final PaginationService paginationService;

  public NotificationService(
      NotificationRepository notificationRepository,
      NotificationMapper notificationMapper,
      MediaDomainService mediaDomainService,
      PaginationService paginationService) {
    this.notificationRepository = notificationRepository;
    this.notificationMapper = notificationMapper;
    this.mediaDomainService = mediaDomainService;
    this.paginationService = paginationService;
  }

  @Transactional(readOnly = true)
  public GetNotificationsResponse getNotifications(
      Long userId, OffsetPaginationRequest pagination) {
    OffsetPaginationResponse<Notification> page =
        paginationService.paginate(
            pageable -> notificationRepository.findByUser(userId, pageable), pagination);

    // 페이지 전체를 한 번에 서명하므로, strict 버전이면 아바타 한 건의 실패가
    // 알림 목록 전체를 404로 만든다. 아바타는 장식이니 실패분만 빼고 내려보낸다.
    var actorProfileImageUrls =
        mediaDomainService.generatePresignedGetUrlsLenient(
            page.content(),
            notification -> {
              var actor = notification.getActor();
              return actor == null ? null : actor.getProfileImage();
            });

    OffsetPaginationResponse<NotificationSummary> notifications =
        page.map(notification -> notificationMapper.toDto(notification, actorProfileImageUrls));

    long unreadCount =
        notificationRepository.countByUser_IdAndStatus(userId, NotificationStatus.UNREAD);

    return new GetNotificationsResponse(notifications, unreadCount);
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
    notificationRepository.markAllAsRead(
        userId, NotificationStatus.UNREAD, NotificationStatus.READ);
  }
}
