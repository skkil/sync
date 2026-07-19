package com.skkil.sync.common.seeder;

import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import com.skkil.sync.comment.service.CommentService;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.service.PostInteractionService;
import com.skkil.sync.project.service.ProjectFollowService;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.service.UserRelationshipService;
import org.springframework.stereotype.Component;

@Component
class SocialGraphSeeder {

  private final PostInteractionService postInteractionService;
  private final PostSeeder postSeeder;
  private final CommentService commentService;
  private final UserRelationshipService userRelationshipService;
  private final ProjectFollowService projectFollowService;

  SocialGraphSeeder(
      PostInteractionService postInteractionService,
      PostSeeder postSeeder,
      CommentService commentService,
      UserRelationshipService userRelationshipService,
      ProjectFollowService projectFollowService) {
    this.postInteractionService = postInteractionService;
    this.postSeeder = postSeeder;
    this.commentService = commentService;
    this.userRelationshipService = userRelationshipService;
    this.projectFollowService = projectFollowService;
  }

  void like(User user, String postSlug) {
    Post post = postSeeder.getBySlug(postSlug);
    SeedSecurityContext.runAs(
        user, () -> postInteractionService.likePost(user.getId(), post.getId()));
  }

  void comment(User author, String postSlug, String content) {
    commentService.createComment(author.getId(), postSlug, new CreateCommentRequest(content));
  }

  void followUser(User follower, User followee) {
    userRelationshipService.followUser(follower.getId(), followee.getId());
  }

  void followProject(User follower, String projectHandle) {
    SeedSecurityContext.runAs(
        follower, () -> projectFollowService.followProject(follower.getId(), projectHandle));
  }
}
