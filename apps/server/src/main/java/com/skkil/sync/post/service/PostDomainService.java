package com.skkil.sync.post.service;

import com.skkil.sync.post.exception.PostNotFoundException;
import com.skkil.sync.post.model.Post;
import com.skkil.sync.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostDomainService {

  private final PostRepository postRepository;

  public PostDomainService(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @Transactional(readOnly = true)
  public Post getPost(Long postId) {
    return postRepository.findById(postId).orElseThrow(() -> new PostNotFoundException(postId));
  }

  @Transactional(readOnly = true)
  public Post getPostBySlug(String slug) {
    return postRepository.findBySlug(slug).orElseThrow(() -> new PostNotFoundException(slug));
  }
}
