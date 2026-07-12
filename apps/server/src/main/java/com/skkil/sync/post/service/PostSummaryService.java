package com.skkil.sync.post.service;

import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostSummaryService {

  private final PostRepository postRepository;

  public PostSummaryService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  /**
   * Persists a system-generated summary (e.g. from the AI summarization listener). Unlike {@link
   * PostService#updatePostSummary}, this is not a user-initiated edit, so it is not gated behind
   * the 'EDIT' permission.
   */
  @Transactional
  public void updateGeneratedSummary(Long postId, String summary) {
    Post post =
        postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));

    post.updateSummary(summary);
  }
}
