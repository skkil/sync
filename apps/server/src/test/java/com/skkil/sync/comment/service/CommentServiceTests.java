package com.skkil.sync.comment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import com.skkil.sync.comment.dto.request.UpdateCommentRequest;
import com.skkil.sync.comment.enums.CommentTargetType;
import com.skkil.sync.comment.exception.CommentNotFoundException;
import com.skkil.sync.comment.exception.InvalidCommentException;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.comment.repository.CommentRepository;
import com.skkil.sync.provider.project.repository.TeamBuildingPostRepository;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentServiceTests {

  @Mock
  private CommentRepository commentRepository;
  @Mock
  private ReflectionRepository reflectionRepository;
  @Mock
  private TeamBuildingPostRepository teamBuildingPostRepository;
  @Mock
  private UserDomainService userDomainService;

  @InjectMocks
  private CommentService commentService;

  @Test
  @DisplayName("[getComments] 루트 댓글 전체 조회")
  void getComments_fetchesAllRootComments() {
    when(commentRepository.findByTargetTypeAndTargetIdAndParentIsNullOrderByCreatedAtAscIdAsc(
            CommentTargetType.REFLECTION, 1L))
        .thenReturn(List.of());

    var response = commentService.getComments(CommentTargetType.REFLECTION, 1L);

    verify(commentRepository)
        .findByTargetTypeAndTargetIdAndParentIsNullOrderByCreatedAtAscIdAsc(
            CommentTargetType.REFLECTION, 1L);
    assertThat(response.comments()).isEmpty();
  }

  @Test
  @DisplayName("[createComment] reflection 대상 댓글 작성")
  void createComment_reflectionTarget_success() {
    CreateCommentRequest request = new CreateCommentRequest(CommentTargetType.REFLECTION, 1L, null, "Comment");
    User author = new User(1L);
    Comment saved = Comment.builder()
            .author(author)
            .targetType(CommentTargetType.REFLECTION)
            .targetId(1L)
            .content("Comment")
            .build();
    saved.setId(10L);

    when(reflectionRepository.existsById(1L)).thenReturn(true);
    when(userDomainService.getUserReference(1L)).thenReturn(author);
    when(commentRepository.save(any(Comment.class))).thenReturn(saved);

    var response = commentService.createComment(1L, request);

    assertThat(response.id()).isEqualTo(10L);
  }

  @Test
  @DisplayName("[createComment] team-building 대상 댓글 작성")
  void createComment_teamBuildingTarget_success() {
    CreateCommentRequest request = new CreateCommentRequest(CommentTargetType.TEAM_BUILDING_POST, 1L, null,
        "Comment");
    User author = new User(1L);
    Comment saved = Comment.builder()
            .author(author)
            .targetType(CommentTargetType.TEAM_BUILDING_POST)
            .targetId(1L)
            .content("Comment")
            .build();
    saved.setId(10L);

    when(teamBuildingPostRepository.existsById(1L)).thenReturn(true);
    when(userDomainService.getUserReference(1L)).thenReturn(author);
    when(commentRepository.save(any(Comment.class))).thenReturn(saved);

    var response = commentService.createComment(1L, request);

    assertThat(response.id()).isEqualTo(10L);
  }

  @Test
  @DisplayName("[createComment] 대상 게시물이 없으면 InvalidCommentException 예외 발생")
  void createComment_targetDoesNotExist_throwInvalidCommentException() {
    CreateCommentRequest request = new CreateCommentRequest(CommentTargetType.REFLECTION, 1L, null, "Comment");

    when(reflectionRepository.existsById(1L)).thenReturn(false);

    assertThatThrownBy(() -> commentService.createComment(1L, request))
        .isInstanceOf(InvalidCommentException.class);
  }

  @Test
  @DisplayName("[createComment] 대댓글 작성")
  void createComment_reply_success() {
    User author = new User(1L);
    Comment parent = Comment.builder()
            .author(author)
            .targetType(CommentTargetType.REFLECTION)
            .targetId(1L)
            .content("Parent")
            .build();
    parent.setId(2L);
    CreateCommentRequest request = new CreateCommentRequest(CommentTargetType.REFLECTION, 1L, 2L, "Reply");
    Comment saved = Comment.builder()
            .author(author)
            .targetType(CommentTargetType.REFLECTION)
            .targetId(1L)
            .parent(parent)
            .content("Reply")
            .build();
    saved.setId(3L);

    when(reflectionRepository.existsById(1L)).thenReturn(true);
    when(commentRepository.findById(2L)).thenReturn(Optional.of(parent));
    when(userDomainService.getUserReference(1L)).thenReturn(author);
    when(commentRepository.save(any(Comment.class))).thenReturn(saved);

    var response = commentService.createComment(1L, request);

    assertThat(response.id()).isEqualTo(3L);
  }

  @Test
  @DisplayName("[createComment] 대댓글의 대상이 부모와 다르면 InvalidCommentException 예외 발생")
  void createComment_replyTargetMismatch_throwInvalidCommentException() {
    User author = new User(1L);
    Comment parent = Comment.builder()
            .author(author)
            .targetType(CommentTargetType.REFLECTION)
            .targetId(1L)
            .content("Parent")
            .build();
    parent.setId(2L);
    CreateCommentRequest request = new CreateCommentRequest(CommentTargetType.TEAM_BUILDING_POST, 1L, 2L, "Reply");

    when(teamBuildingPostRepository.existsById(1L)).thenReturn(true);
    when(commentRepository.findById(2L)).thenReturn(Optional.of(parent));

    assertThatThrownBy(() -> commentService.createComment(1L, request))
        .isInstanceOf(InvalidCommentException.class);
  }

  @Test
  @DisplayName("[updateComment] 삭제된 댓글은 수정할 수 없음")
  void updateComment_deletedComment_throwInvalidCommentException() {
    Comment comment = Comment.builder()
            .author(new User(1L))
            .targetType(CommentTargetType.REFLECTION)
            .targetId(1L)
            .content("Comment")
            .build();
    comment.delete();

    when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

    assertThatThrownBy(() -> commentService.updateComment(1L, new UpdateCommentRequest("Updated")))
        .isInstanceOf(InvalidCommentException.class);
  }

  @Test
  @DisplayName("[deleteComment] 댓글 소프트 삭제")
  void deleteComment_success() {
    Comment comment = Comment.builder()
            .author(new User(1L))
            .targetType(CommentTargetType.REFLECTION)
            .targetId(1L)
            .content("Comment")
            .build();

    when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

    commentService.deleteComment(1L);

    assertThat(comment.isDeleted()).isTrue();
  }

  @Test
  @DisplayName("[deleteComment] 존재하지 않는 댓글이면 CommentNotFoundException 예외 발생")
  void deleteComment_notFound_throwCommentNotFoundException() {
    when(commentRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> commentService.deleteComment(1L))
        .isInstanceOf(CommentNotFoundException.class);
  }
}
