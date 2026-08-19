package com.skkil.sync.notification.listener;

import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.notification.constant.NotificationStatus;
import com.skkil.sync.notification.constant.NotificationType;
import com.skkil.sync.notification.repository.NotificationRepository;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.post.service.PostInteractionService;
import com.skkil.sync.user.constant.Role;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import java.time.Instant;
import java.util.function.BooleanSupplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

/**
 * 좋아요 → 알림 저장까지의 전 구간(멱등 UPSERT, 커밋 후 리스너, 비동기 프로세서)을 실제 빈 위에서 검증한다. PUT이 멱등이므로 같은 사용자가 반복해 눌러도 알림은
 * 한 번만 쌓여야 한다.
 */
@Import(TestcontainersConfig.class)
@SpringBootTest
@ActiveProfiles("dev")
@TestPropertySource(properties = {"app.seed.enabled=false", "app.websocket.enabled=false"})
class PostLikeNotificationIntegrationTests {

  @Autowired private PostInteractionService postInteractionService;
  @Autowired private NotificationRepository notificationRepository;
  @Autowired private UserRepository userRepository;
  @Autowired private PostRepository postRepository;

  private User author;
  private User liker;
  private User secondLiker;
  private Post post;

  @BeforeEach
  void setUp() {
    author = saveUser("like-author");
    liker = saveUser("like-liker");
    secondLiker = saveUser("like-second");

    Post fixture =
        Post.builder()
            .slug("like-notification-" + System.nanoTime())
            .author(author)
            .title("좋아요 알림 테스트")
            .type(PostType.LONG)
            .status(PostStatus.PUBLISHED)
            .jsonContent("본문")
            .build();
    fixture.updateJsonContent("본문", "본문", 0);
    post = postRepository.save(fixture);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("좋아요가 알림으로 저장되고, 같은 사용자의 반복 호출은 알림을 늘리지 않는다")
  void likePost_notifiesAuthorOncePerLiker() {
    authenticateAs(liker);
    postInteractionService.likePost(liker.getId(), post.getId());

    awaitUnreadCount(1);
    assertThat(
            notificationRepository
                .findByUser(author.getId(), PageRequest.of(0, 5))
                .getContent()
                .getFirst()
                .getType())
        .isEqualTo(NotificationType.NEW_LIKE);

    // 같은 사용자의 재호출(멱등 PUT)은 이벤트를 내지 않아야 한다. 부정 대기 대신,
    // 두 번째 사용자의 좋아요가 도착한 시점에 총 개수가 2(3이 아님)임을 단언한다.
    postInteractionService.likePost(liker.getId(), post.getId());

    authenticateAs(secondLiker);
    postInteractionService.likePost(secondLiker.getId(), post.getId());

    awaitUnreadCount(2);
    assertThat(unreadCount()).isEqualTo(2);
  }

  private void authenticateAs(User user) {
    AuthenticatedUser principal =
        AuthenticatedUser.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .role(Role.USER)
            .enabled(true)
            .build();
    SecurityContextHolder.getContext()
        .setAuthentication(
            UsernamePasswordAuthenticationToken.authenticated(
                principal, null, principal.getAuthorities()));
  }

  private User saveUser(String prefix) {
    return userRepository.save(
        User.builder()
            .email(prefix + "-" + System.nanoTime() + "@example.com")
            .fullName("좋아요 테스트")
            .hashedPassword("hash")
            .build());
  }

  private long unreadCount() {
    return notificationRepository.countByUser_IdAndStatus(
        author.getId(), NotificationStatus.UNREAD);
  }

  private void awaitUnreadCount(long expected) {
    await("알림 " + expected + "건 저장", () -> unreadCount() == expected);
  }

  /** 비동기({@code @Async}) 처리 완료를 폴링으로 기다린다 (Awaitility 미도입). */
  private static void await(String what, BooleanSupplier condition) {
    Instant deadline = Instant.now().plusSeconds(10);
    while (Instant.now().isBefore(deadline)) {
      if (condition.getAsBoolean()) {
        return;
      }
      try {
        Thread.sleep(50);
      } catch (InterruptedException interrupted) {
        Thread.currentThread().interrupt();
        throw new IllegalStateException(interrupted);
      }
    }
    throw new AssertionError(what + " 대기가 10초 안에 끝나지 않았다");
  }
}
