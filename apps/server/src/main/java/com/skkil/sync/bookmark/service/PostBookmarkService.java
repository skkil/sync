package com.skkil.sync.bookmark.service;

import com.skkil.sync.bookmark.repository.PostBookmarkRepository;
import com.skkil.sync.post.service.PostDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostBookmarkService {

  private final PostBookmarkRepository postBookmarkRepository;
  private final PostDomainService postDomainService;

  public PostBookmarkService(
      PostBookmarkRepository postBookmarkRepository, PostDomainService postDomainService) {
    this.postBookmarkRepository = postBookmarkRepository;
    this.postDomainService = postDomainService;
  }

  @Transactional
  public void bookmarkPost(Long userId, Long postId) {
    postDomainService.getPost(postId);
    postBookmarkRepository.insertIfAbsent(userId, postId);
  }

  @Transactional
  public void unbookmarkPost(Long userId, Long postId) {
    postBookmarkRepository.deleteByUser_IdAndPost_Id(userId, postId);
  }
}
