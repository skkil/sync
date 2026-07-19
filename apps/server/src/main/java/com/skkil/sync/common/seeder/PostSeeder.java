package com.skkil.sync.common.seeder;

import com.skkil.sync.post.dto.request.CreatePostRequest;
import com.skkil.sync.post.dto.request.CreateProjectPostRequest;
import com.skkil.sync.post.dto.request.PostContentRequest;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.model.PostType;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.post.service.PostService;
import com.skkil.sync.user.model.User;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
class PostSeeder {

  private static final String EMPTY_TIPTAP_DOCUMENT = "{\"type\":\"doc\",\"content\":[]}";

  private final PostService postService;
  private final PostRepository postRepository;

  PostSeeder(PostService postService, PostRepository postRepository) {
    this.postService = postService;
    this.postRepository = postRepository;
  }

  String seed(
      User author,
      String title,
      PostType type,
      String content,
      List<String> tags,
      String projectHandle) {
    if (projectHandle == null) {
      PostContentRequest requestContent =
          new PostContentRequest(content, EMPTY_TIPTAP_DOCUMENT, List.of());

      CreatePostRequest request =
          CreatePostRequest.builder()
              .title(title)
              .type(type)
              .content(requestContent)
              .tags(tags)
              .build();

      return postService.createPost(author.getId(), request).slug();
    }

    PostContentRequest requestContent =
        new PostContentRequest(content, EMPTY_TIPTAP_DOCUMENT, List.of());

    CreateProjectPostRequest request =
        CreateProjectPostRequest.builder()
            .title(title)
            .type(type)
            .content(requestContent)
            .tags(tags)
            .build();

    return SeedSecurityContext.runAs(
        author, () -> postService.createProjectPost(author.getId(), projectHandle, request).slug());
  }

  Post getBySlug(String slug) {
    return postRepository.findBySlug(slug).orElseThrow();
  }
}
