package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.event.PostLikedEvent;
import com.skkil.sync.post.mapper.PostAssembler;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.repository.PostLikeRepository;
import com.skkil.sync.post.repository.PostQueryRepository;
import com.skkil.sync.post.repository.pagination.LikedPostCursorPaginationProvider;
import com.skkil.sync.user.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

@ExtendWith(MockitoExtension.class)
class PostInteractionServiceTests {

  @Mock private PostLikeRepository postLikeRepository;
  @Mock private PostQueryRepository postQueryRepository;
  @Mock private PostAssembler postAssembler;
  @Mock private LikedPostCursorPaginationProvider paginationProvider;
  @Mock private PaginationService paginationService;
  @Mock private PostDomainService postDomainService;
  @Mock private ApplicationEventPublisher eventPublisher;

  @Test
  void likePost_newLike_publishesPostLikedEvent() {
    when(postLikeRepository.insertAndIncrementIfAbsent(2L, 10L)).thenReturn(1);
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

    service().likePost(2L, 10L);

    ArgumentCaptor<PostLikedEvent> captor = ArgumentCaptor.forClass(PostLikedEvent.class);
    verify(eventPublisher).publishEvent(captor.capture());
    assertThat(captor.getValue().getPostId()).isEqualTo(10L);
    assertThat(captor.getValue().getPostAuthorId()).isEqualTo(1L);
    assertThat(captor.getValue().getLikerId()).isEqualTo(2L);
  }

  /** PUT이 멱등이므로 알림도 멱등이어야 한다 — 이미 눌린 좋아요는 이벤트를 내지 않는다. */
  @Test
  void likePost_duplicateLike_publishesNothing() {
    when(postLikeRepository.insertAndIncrementIfAbsent(2L, 10L)).thenReturn(0);

    service().likePost(2L, 10L);

    verify(eventPublisher, never()).publishEvent(any());
    verify(postDomainService, never()).getPost(any());
  }

  private PostInteractionService service() {
    return new PostInteractionService(
        postLikeRepository,
        postQueryRepository,
        postAssembler,
        paginationProvider,
        paginationService,
        postDomainService,
        eventPublisher);
  }
}
