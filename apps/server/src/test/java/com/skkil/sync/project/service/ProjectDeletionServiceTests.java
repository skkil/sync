package com.skkil.sync.project.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.skkil.sync.comment.model.Comment;
import com.skkil.sync.comment.repository.CommentRepository;
import com.skkil.sync.common.config.TestcontainersConfig;
import com.skkil.sync.config.JpaConfig;
import com.skkil.sync.media.enums.MediaStatus;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.repository.MediaRepository;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostMediaFile;
import com.skkil.sync.post.model.PostStatus;
import com.skkil.sync.post.model.PostTag;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.model.Tag;
import com.skkil.sync.post.model.TagFollowRelationship;
import com.skkil.sync.post.repository.PostMediaFileRepository;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.post.repository.TagFollowRelationshipRepository;
import com.skkil.sync.post.repository.TagRepository;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import com.skkil.sync.project.repository.ProjectRepository;
import com.skkil.sync.project.repository.TeammateRepository;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfig.class, JpaConfig.class, ProjectDeletionService.class})
class ProjectDeletionServiceTests {

  @Autowired private ProjectDeletionService projectDeletionService;

  @Autowired private ProjectRepository projectRepository;

  @Autowired private TeammateRepository teammateRepository;

  @Autowired private PostRepository postRepository;

  @Autowired private PostMediaFileRepository postMediaFileRepository;

  @Autowired private CommentRepository commentRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private TagFollowRelationshipRepository tagFollowRelationshipRepository;

  @Autowired private MediaRepository mediaRepository;

  @Autowired private UserRepository userRepository;

  @Autowired private EntityManager entityManager;

  @Test
  @DisplayName("[delete] 프로젝트 게시글과 댓글을 삭제하고 글로벌 태그 및 미디어를 정리한다")
  void delete_removesProjectPostsAndCleansRelatedData() {
    User owner = saveUser();
    Media icon = saveUploadedMedia(owner, "project-icon");
    Media cover = saveUploadedMedia(owner, "post-cover");
    Media body = saveUploadedMedia(owner, "post-body");

    Project project =
        Project.builder().handle("project-to-delete").name("삭제할 프로젝트").isPublic(false).build();
    project.setIcon(icon);
    project = projectRepository.saveAndFlush(project);
    teammateRepository.saveAndFlush(Teammate.owner(project, owner));

    Tag globalTag = tagRepository.saveAndFlush(Tag.builder().name("global-tag").build());
    Tag sharedGlobalTag =
        tagRepository.saveAndFlush(Tag.builder().name("shared-global-tag").build());
    Tag projectTag =
        tagRepository.saveAndFlush(Tag.builder().name("project-tag").project(project).build());

    Post post =
        savePost(
            "project-post-to-delete",
            owner,
            project,
            cover,
            List.of(globalTag, sharedGlobalTag, projectTag));
    Post retainedPost =
        savePost("personal-post-to-keep", owner, null, null, List.of(sharedGlobalTag));
    postMediaFileRepository.saveAndFlush(new PostMediaFile(post, body, 0));
    Comment comment =
        commentRepository.saveAndFlush(
            Comment.builder().author(owner).post(post).content("댓글").build());
    User follower =
        userRepository.saveAndFlush(
            User.builder().email("tag-follower@example.com").fullName("Tag Follower").build());
    tagFollowRelationshipRepository.saveAndFlush(
        TagFollowRelationship.builder().follower(follower).tag(sharedGlobalTag).build());
    tagRepository.incrementPostCount(globalTag);
    tagRepository.incrementPostCount(sharedGlobalTag);
    tagRepository.incrementPostCount(sharedGlobalTag);
    tagRepository.incrementFollowerCount(sharedGlobalTag);
    tagRepository.flush();

    Long projectId = project.getId();
    Long postId = post.getId();
    Long retainedPostId = retainedPost.getId();
    Long commentId = comment.getId();
    Long projectTagId = projectTag.getId();
    List<Long> mediaIds = List.of(icon.getId(), cover.getId(), body.getId());

    entityManager.clear();
    Project projectToDelete = projectRepository.findById(projectId).orElseThrow();

    projectDeletionService.delete(projectToDelete);
    entityManager.clear();

    assertThat(projectRepository.existsById(projectId)).isFalse();
    assertThat(postRepository.existsById(postId)).isFalse();
    assertThat(postRepository.existsById(retainedPostId)).isTrue();
    assertThat(commentRepository.existsById(commentId)).isFalse();
    assertThat(tagRepository.existsById(projectTagId)).isFalse();
    assertThat(tagRepository.findById(globalTag.getId()).orElseThrow().getPostCount()).isZero();
    Tag retainedSharedGlobalTag = tagRepository.findById(sharedGlobalTag.getId()).orElseThrow();
    assertThat(retainedSharedGlobalTag.getPostCount()).isOne();
    assertThat(retainedSharedGlobalTag.getFollowerCount()).isOne();
    assertThat(mediaRepository.findAllByIdIn(mediaIds))
        .extracting(Media::getStatus)
        .containsOnly(MediaStatus.DELETED);
  }

  private User saveUser() {
    return userRepository.saveAndFlush(
        User.builder().email("project-owner@example.com").fullName("프로젝트 소유자").build());
  }

  private Media saveUploadedMedia(User uploader, String key) {
    Media media =
        Media.builder()
            .uploader(uploader)
            .mediaType("image/png")
            .bucket("test-bucket")
            .key(key)
            .fileName(key + ".png")
            .fileSize(100L)
            .build();
    media.markAsUploaded();
    return mediaRepository.saveAndFlush(media);
  }

  private Post savePost(
      String slug, User author, @Nullable Project project, @Nullable Media cover, List<Tag> tags) {
    Post post =
        Post.builder()
            .slug(slug)
            .author(author)
            .project(project)
            .coverMedia(cover)
            .title("삭제할 게시글")
            .type(PostType.LONG)
            .status(PostStatus.PUBLISHED)
            .content("본문")
            .build();
    post.updateContent("본문", "본문", 1);
    tags.forEach(tag -> post.addTag(PostTag.builder().post(post).tag(tag).build()));
    return postRepository.saveAndFlush(post);
  }
}
