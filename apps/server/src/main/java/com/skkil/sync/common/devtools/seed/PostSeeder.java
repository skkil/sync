package com.skkil.sync.common.devtools.seed;

import com.skkil.sync.post.dto.request.CreatePostRequest;
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
    CreatePostRequest.Content requestContent =
        new CreatePostRequest.Content(content, EMPTY_TIPTAP_DOCUMENT, List.of());
    CreatePostRequest.Project project =
        projectHandle == null ? null : new CreatePostRequest.Project(projectHandle);

    CreatePostRequest request =
        CreatePostRequest.builder()
            .title(title)
            .type(type)
            .content(requestContent)
            .tags(tags)
            .project(project)
            .build();

    return postService.createPost(author.getId(), request).slug();
  }

  Post getBySlug(String slug) {
    return postRepository.findBySlug(slug).orElseThrow();
  }
}
