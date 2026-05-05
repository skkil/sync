package com.skkil.sync.reflection.service;

import com.skkil.sync.reflection.exception.ReflectionNotFoundException;
import com.skkil.sync.reflection.model.Reflection;
import com.skkil.sync.reflection.repository.ReflectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReflectionDomainService {

  private final ReflectionRepository reflectionRepository;

  public ReflectionDomainService(ReflectionRepository reflectionRepository) {
    this.reflectionRepository = reflectionRepository;
  }

  @Transactional(readOnly = true)
  public Reflection getReflection(Long reflectionId) {
    return reflectionRepository
        .findById(reflectionId)
        .orElseThrow(() -> new ReflectionNotFoundException(reflectionId));
  }

  @Transactional(readOnly = true)
  public Reflection getReflectionBySlug(String slug) {
    return reflectionRepository
        .findBySlug(slug)
        .orElseThrow(() -> new ReflectionNotFoundException(slug));
  }
}
