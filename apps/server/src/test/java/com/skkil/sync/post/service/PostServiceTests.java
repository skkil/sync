package com.skkil.sync.post.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.skkil.sync.post.dto.request.UpdatePostRequest;
import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.repository.PostRepository;
import com.skkil.sync.post.snippets.UpdatePostRequestSnippets;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PostServiceTests {

  @Mock private PostRepository postRepository;

  @InjectMocks private PostService postService;

  @Test
  @DisplayName("[updatePost] 존재하지 않는 회고를 수정하려는 경우 PostNotFoundException 예외 발생")
  void updatePost_postNotFound_throwsException() {
    Long postId = 1L;
    UpdatePostRequest request = UpdatePostRequestSnippets.getUpdatePostRequest();

    when(postRepository.findById(postId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> postService.updatePost(postId, request))
        .isInstanceOf(PostNotFoundException.class);
  }

  @Test
  @DisplayName("[deletePost] 존재하지 않는 회고를 삭제하려는 경우 PostNotFoundException 예외 발생")
  void deletePost_postNotFound_throwsException() {
    Long postId = 1L;

    when(postRepository.existsById(postId)).thenReturn(false);

    assertThatThrownBy(() -> postService.deletePost(postId))
        .isInstanceOf(PostNotFoundException.class);
  }
}
