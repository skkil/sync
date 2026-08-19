package com.skkil.sync.notification.listener;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.notification.constant.NotificationEntityType;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.event.NotificationEvent;
import com.skkil.sync.notification.model.NewLikePayload;
import com.skkil.sync.post.event.PostLikedEvent;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.service.PostDomainService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class PostLikedNotificationListenerTests {

  @Mock private ApplicationEventPublisher eventPublisher;
  @Mock private UserDomainService userDomainService;
  @Mock private PostDomainService postDomainService;

  @InjectMocks private PostLikedNotificationListener listener;

  @Test
  void handlePostLikedEvent_selfLike_publishesNothing() {
    listener.handlePostLikedEvent(new PostLikedEvent(10L, 1L, 1L));

    verify(eventPublisher, never()).publishEvent(any());
  }

  @Test
  void handlePostLikedEvent_publishesNewLikeNotification() {
    User liker =
        User.builder().email("liker@example.com").fullName("좋아요 누른 사람").hashedPassword("h").build();
    when(userDomainService.getUser(2L)).thenReturn(liker);
    Post post =
        Post.builder()
            .slug("liked-post")
            .author(new User(1L))
            .title("좋아요 대상")
            .type(PostType.LONG)
            .status(PostStatus.PUBLISHED)
            .jsonContent("본문")
            .build();
    when(postDomainService.getPost(10L)).thenReturn(post);

    listener.handlePostLikedEvent(new PostLikedEvent(10L, 1L, 2L));

    ArgumentCaptor<NotificationEvent> captor = ArgumentCaptor.forClass(NotificationEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    NotificationEvent published = captor.getValue();
    assertThat(published.getRecipientId()).isEqualTo(1L);
    assertThat(published.getNotificationType()).isEqualTo(NotificationType.NEW_LIKE);
    assertThat(published.getActorId()).isEqualTo(2L);
    assertThat(published.getEntityType()).isEqualTo(NotificationEntityType.POST);
    assertThat(published.getEntityId()).isEqualTo(10L);
    assertThat(published.getPayload())
        .isEqualTo(new NewLikePayload(liker.getHandle(), "좋아요 누른 사람", "좋아요 대상", "liked-post"));
  }
}
