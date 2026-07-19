package com.skkil.sync.comment.service;

import com.skkil.sync.comment.dto.data.CommentDto;
import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import com.skkil.sync.comment.dto.request.UpdateCommentRequest;
import com.skkil.sync.comment.dto.response.CreateCommentResponse;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.comment.exception.CommentNotFoundException;
import com.skkil.sync.comment.mapper.CommentAssembler;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.comment.repository.CommentQueryRepository;
import com.skkil.sync.comment.repository.CommentRepository;
import com.skkil.sync.comment.repository.pagination.CommentCursorPaginationProvider;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.service.PostDomainService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final CommentQueryRepository commentQueryRepository;
  private final PostDomainService postDomainService;
  private final UserDomainService userDomainService;
  private final PaginationService paginationService;
  private final CommentCursorPaginationProvider paginationProvider;
  private final CommentAssembler commentAssembler;

  public CommentService(
      CommentRepository commentRepository,
      CommentQueryRepository commentQueryRepository,
      PostDomainService postDomainService,
      UserDomainService userDomainService,
      PaginationService paginationService,
      CommentCursorPaginationProvider paginationProvider,
      CommentAssembler commentAssembler) {
    this.commentRepository = commentRepository;
    this.commentQueryRepository = commentQueryRepository;
    this.postDomainService = postDomainService;
    this.userDomainService = userDomainService;
    this.paginationService = paginationService;
    this.paginationProvider = paginationProvider;
    this.commentAssembler = commentAssembler;
  }

  @Transactional(readOnly = true)
  public GetCommentsResponse getPostComments(String slug, CursorPaginationRequest pagination) {
    Post post = postDomainService.getPublicPublishedPostBySlug(slug);

    CursorPaginationResponse<CommentDto> comments =
        paginationService.paginate(
            commentQueryRepository.getCommentsByPost(post.getId()), paginationProvider, pagination);

    return commentAssembler.toGetCommentsResponse(comments, post.getAuthor().getId());
  }

  @Transactional
  public CreateCommentResponse createComment(
      Long authorId, String slug, CreateCommentRequest request) {
    User author = userDomainService.getUserReference(authorId);
    Post post = postDomainService.getPublicPublishedPostBySlug(slug);

    Comment comment =
        Comment.builder().author(author).post(post).content(request.content()).build();

    comment = commentRepository.save(comment);
    return new CreateCommentResponse(comment.getId());
  }

  @Transactional
  @PreAuthorize("hasPermission(#commentId, 'COMMENT', 'EDIT')")
  public void updateComment(Long commentId, UpdateCommentRequest request) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException(commentId));

    comment.updateContent(request.content());
  }

  @Transactional
  @PreAuthorize("hasPermission(#commentId, 'COMMENT', 'DELETE')")
  public void deleteComment(Long commentId) {
    commentRepository
        .findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId))
        .delete();
    ;
  }

  @Transactional
  @PreAuthorize("hasPermission(@commentService.resolvePostId(#commentId), 'POST', 'EDIT')")
  public void acceptComment(Long commentId) {
    setAccepted(commentId, true);
  }

  @Transactional
  @PreAuthorize("hasPermission(@commentService.resolvePostId(#commentId), 'POST', 'EDIT')")
  public void unacceptComment(Long commentId) {
    setAccepted(commentId, false);
  }

  // @PreAuthorize는 메서드 본문 실행 전에 평가되므로, POST 'EDIT' 권한(게시글 작성자만 허용)을
  // 검사하려면 댓글이 속한 게시글 ID를 미리 조회해야 한다.
  public Long resolvePostId(Long commentId) {
    return commentRepository
        .findById(commentId)
        .orElseThrow(() -> new CommentNotFoundException(commentId))
        .getPost()
        .getId();
  }

  private void setAccepted(Long commentId, boolean accepted) {
    Comment comment =
        commentRepository
            .findById(commentId)
            .orElseThrow(() -> new CommentNotFoundException(commentId));

    if (accepted) {
      comment.accept();
    } else {
      comment.unaccept();
    }
  }
}
