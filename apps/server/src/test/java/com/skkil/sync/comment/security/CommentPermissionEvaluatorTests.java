package com.skkil.sync.comment.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.comment.repository.CommentRepository;
import com.skkil.sync.common.security.PermissionOperation;
import com.skkil.sync.common.security.WithAuthenticatedUserSecurityContextFactory;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CommentPermissionEvaluatorTests {

  @Mock private CommentRepository commentRepository;

  @InjectMocks private CommentPermissionEvaluator permissionEvaluator;

  @Test
  @DisplayName("[hasPermission] 댓글 수정 또는 삭제를 null 사용자로 요청하면 권한이 없는 것으로 반환")
  void hasPermission_requestEditOrDelete_userIsAuthor_returnTrue() {
    Long commentId = 1L;

    Comment comment = Comment.builder().build();
    comment.delete();
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    assertThat(permissionEvaluator.hasPermission(null, commentId, PermissionOperation.EDIT))
        .isFalse();

    assertThat(permissionEvaluator.hasPermission(null, commentId, PermissionOperation.DELETE))
        .isFalse();
  }

  @Test
  @DisplayName("[hasPermission] 댓글이 삭제된 경우 수정 또는 삭제 요청을 하면 권한이 없는 것으로 반환")
  void hasPermission_requestEditOrDelete_commentIsDeleted_returnFalse() {
    Long commentId = 1L;
    AuthenticatedUser user = WithAuthenticatedUserSecurityContextFactory.createAuthenticatedUser();

    Comment comment = Comment.builder().build();
    comment.delete();
    when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

    assertThat(permissionEvaluator.hasPermission(user, commentId, PermissionOperation.EDIT))
        .isFalse();

    assertThat(permissionEvaluator.hasPermission(user, commentId, PermissionOperation.DELETE))
        .isFalse();
  }
}
