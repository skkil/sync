package com.skkil.sync.notification.listener;

import com.skkil.sync.notification.constant.NotificationEntityType;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.notification.model.NewLikePayload;
import com.skkil.sync.post.event.PostLikedEvent;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.service.PostDomainService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 좋아요마다 알림 1건을 만든다. 발행 측({@code PostInteractionService})이 실제로 새 좋아요일 때만 이벤트를 내므로, 같은 사용자가 반복해 눌러도
 * 알림이 중복되지 않는다.
 *
 * <p>집계("외 N명")나 미읽음 중복 억제는 의도적으로 하지 않았다 — 좋아요한 사람 목록을 조회할 API가 없어, 알림에서 빠진 이름은 어디서도 복구되지 않기 때문이다.
 * 인기 글의 알림 볼륨이 실제 문제가 되면 그때 payload에 집계를 담는 방식으로 전환한다.
 */
@Component
@Slf4j
public class PostLikedNotificationListener {

  private final ApplicationEventPublisher eventPublisher;
  private final UserDomainService userDomainService;
  private final PostDomainService postDomainService;

  public PostLikedNotificationListener(
      ApplicationEventPublisher eventPublisher,
      UserDomainService userDomainService,
      PostDomainService postDomainService) {
    this.eventPublisher = eventPublisher;
    this.userDomainService = userDomainService;
    this.postDomainService = postDomainService;
  }

  @Async
  @TransactionalEventListener
  public void handlePostLikedEvent(PostLikedEvent event) {
    log.debug("Post liked event received for post ID: {}", event.getPostId());

    if (event.getLikerId().equals(event.getPostAuthorId())) {
      // 자기 글 좋아요는 알리지 않는다 (댓글 리스너와 동일 규약).
      return;
    }

    User liker = userDomainService.getUser(event.getLikerId());
    Post post = postDomainService.getPost(event.getPostId());

    eventPublisher.publishEvent(
        new NotificationEvent(
            event.getPostAuthorId(),
            NotificationType.NEW_LIKE,
            event.getLikerId(),
            NotificationEntityType.POST,
            event.getPostId(),
            new NewLikePayload(
                liker.getHandle(), liker.getFullName(), post.getTitle(), post.getSlug())));
  }
}
