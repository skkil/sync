package com.skkil.sync.comment.service;

import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import com.skkil.sync.comment.dto.request.UpdateCommentRequest;
import com.skkil.sync.comment.dto.response.CreateCommentResponse;
import com.skkil.sync.comment.dto.response.GetCommentsResponse;
import com.skkil.sync.comment.exception.CommentNotFoundException;
import com.skkil.sync.comment.mapper.CommentMapper;
import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.comment.repository.CommentRepository;
import com.skkil.sync.media.service.domain.MediaDomainService;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.service.PostDomainService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.domain.UserDomainService;
import java.net.URL;
import java.util.List;
import java.util.Map;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {

  private final CommentRepository commentRepository;
  private final PostDomainService postDomainService;
  private final UserDomainService userDomainService;
  private final MediaDomainService mediaDomainService;
  private final CommentMapper commentMapper;

  public CommentService(
      CommentRepository commentRepository,
      PostDomainService postDomainService,
      UserDomainService userDomainService,
      MediaDomainService mediaDomainService,
      CommentMapper commentMapper) {
    this.commentRepository = commentRepository;
    this.postDomainService = postDomainService;
    this.userDomainService = userDomainService;
    this.mediaDomainService = mediaDomainService;
    this.commentMapper = commentMapper;
  }

  @Transactional(readOnly = true)
  public GetCommentsResponse getPostComments(String slug) {
    Post post = postDomainService.getPostBySlug(slug);

    List<Comment> comments = commentRepository.findByPost(post);

    Map<Long, URL> profileImageUrls =
        mediaDomainService.generatePublicGetUrls(
            comments, comment -> comment.getAuthor().getProfileImage());

    return commentMapper.toGetCommentsResponse(post, comments, profileImageUrls);
  }

  @Transactional
  public CreateCommentResponse createComment(
      Long authorId, String slug, CreateCommentRequest request) {
    User author = userDomainService.getUserReference(authorId);
    Post post = postDomainService.getPostBySlug(slug);

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
}
