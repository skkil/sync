package com.skkil.sync.bookmark.service;

import com.skkil.sync.bookmark.repository.ReflectionBookmarkRepository;
import com.skkil.sync.reflection.service.ReflectionDomainService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReflectionBookmarkService {

  private final ReflectionBookmarkRepository reflectionBookmarkRepository;
  private final ReflectionDomainService reflectionDomainService;

  public ReflectionBookmarkService(
      ReflectionBookmarkRepository reflectionBookmarkRepository,
      ReflectionDomainService reflectionDomainService) {
    this.reflectionBookmarkRepository = reflectionBookmarkRepository;
    this.reflectionDomainService = reflectionDomainService;
  }

  @Transactional
  public void bookmarkReflection(Long userId, Long reflectionId) {
    reflectionDomainService.getReflection(reflectionId);
    reflectionBookmarkRepository.insertIfAbsent(userId, reflectionId);
  }

  @Transactional
  public void unbookmarkReflection(Long userId, Long reflectionId) {
    reflectionBookmarkRepository.deleteByUser_IdAndReflection_Id(userId, reflectionId);
  }
}
