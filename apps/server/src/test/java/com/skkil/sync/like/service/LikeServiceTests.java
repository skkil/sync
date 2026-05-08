package com.skkil.sync.like.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.like.dto.request.CreateLikeRequest;
import com.skkil.sync.like.enums.LikeTargetType;
import com.skkil.sync.like.exception.InvalidLikeException;
import com.skkil.sync.like.model.Like;
import com.skkil.sync.like.repository.LikeRepository;
import com.skkil.sync.provider.project.repository.TeamBuildingPostRepository;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class LikeServiceTests {

  @Mock private LikeRepository likeRepository;
  @Mock private ReflectionRepository reflectionRepository;
  @Mock private TeamBuildingPostRepository teamBuildingPostRepository;
  @Mock private com.skkil.sync.comment.repository.CommentRepository commentRepository;
  @Mock private UserDomainService userDomainService;

  private LikeService likeService;

  @BeforeEach
  void setUp() {
    likeService =
        new LikeService(
            likeRepository,
            reflectionRepository,
            teamBuildingPostRepository,
            commentRepository,
            userDomainService);
  }

  @Test
  @DisplayName("[like] reflection 좋아요 생성 시 카운트 증가")
  void like_reflection_success() {
    CreateLikeRequest request = new CreateLikeRequest(LikeTargetType.REFLECTION, 1L);
    User user = new User(1L);
    Reflection reflection = Reflection.builder().author(user).content("Reflection").build();

    when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(1L, LikeTargetType.REFLECTION, 1L))
        .thenReturn(false);
    when(userDomainService.getUserReference(1L)).thenReturn(user);
    when(reflectionRepository.findById(1L)).thenReturn(Optional.of(reflection));

    var response = likeService.like(1L, request);

    verify(likeRepository).saveAndFlush(any(Like.class));
    assertThat(response.liked()).isTrue();
    assertThat(response.likeCount()).isEqualTo(1L);
    assertThat(reflection.getLikeCount()).isEqualTo(1L);
  }

  @Test
  @DisplayName("[like] 중복 insert 예외는 서비스에서 잡지 않음")
  void like_duplicateInsert_propagatesException() {
    CreateLikeRequest request = new CreateLikeRequest(LikeTargetType.REFLECTION, 1L);
    User user = new User(1L);
    Reflection reflection = Reflection.builder().author(user).content("Reflection").build();

    when(reflectionRepository.findById(1L)).thenReturn(Optional.of(reflection));
    when(likeRepository.existsByUserIdAndTargetTypeAndTargetId(1L, LikeTargetType.REFLECTION, 1L))
        .thenReturn(false);
    when(userDomainService.getUserReference(1L)).thenReturn(user);
    when(likeRepository.saveAndFlush(any(Like.class)))
        .thenThrow(new DataIntegrityViolationException("duplicate like"));

    assertThatThrownBy(() -> likeService.like(1L, request))
        .isInstanceOf(DataIntegrityViolationException.class);
  }

  @Test
  @DisplayName("[like] 삭제된 댓글은 좋아요할 수 없음")
  void like_deletedComment_throwInvalidLikeException() {
    CreateLikeRequest request = new CreateLikeRequest(LikeTargetType.COMMENT, 1L);
    Comment comment =
        Comment.builder()
            .author(new User(1L))
            .targetType(com.skkil.sync.comment.enums.CommentTargetType.REFLECTION)
            .targetId(1L)
            .content("Comment")
            .build();
    comment.delete();

    when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

    assertThatThrownBy(() -> likeService.like(1L, request))
        .isInstanceOf(InvalidLikeException.class)
        .hasMessage("Deleted comment cannot be liked.");
    verify(likeRepository, never()).existsByUserIdAndTargetTypeAndTargetId(any(), any(), any());
    verify(likeRepository, never()).saveAndFlush(any(Like.class));
  }

  @Test
  @DisplayName("[unlike] 내가 누른 reflection 좋아요 삭제 시 카운트 감소")
  void unlike_reflection_success() {
    CreateLikeRequest request = new CreateLikeRequest(LikeTargetType.REFLECTION, 1L);
    User user = new User(1L);
    Reflection reflection = Reflection.builder().author(user).content("Reflection").build();
    reflection.incrementLikeCount();
    Like like =
        Like.builder().user(user).targetType(LikeTargetType.REFLECTION).targetId(1L).build();

    when(reflectionRepository.findById(1L)).thenReturn(Optional.of(reflection));
    when(likeRepository.findByUserIdAndTargetTypeAndTargetId(1L, LikeTargetType.REFLECTION, 1L))
        .thenReturn(Optional.of(like));

    var response = likeService.unlike(1L, request);

    verify(likeRepository).delete(like);
    assertThat(response.liked()).isFalse();
    assertThat(response.likeCount()).isZero();
    assertThat(reflection.getLikeCount()).isZero();
  }

  @Test
  @DisplayName("[unlike] 내가 누른 좋아요가 없으면 삭제하지 않고 성공 응답")
  void unlike_notLiked_noop() {
    CreateLikeRequest request = new CreateLikeRequest(LikeTargetType.REFLECTION, 1L);
    User user = new User(1L);
    Reflection reflection = Reflection.builder().author(user).content("Reflection").build();

    when(reflectionRepository.findById(1L)).thenReturn(Optional.of(reflection));
    when(likeRepository.findByUserIdAndTargetTypeAndTargetId(1L, LikeTargetType.REFLECTION, 1L))
        .thenReturn(Optional.empty());

    var response = likeService.unlike(1L, request);

    verify(likeRepository, never()).delete(any(Like.class));
    assertThat(response.liked()).isFalse();
    assertThat(response.likeCount()).isZero();
  }
}
