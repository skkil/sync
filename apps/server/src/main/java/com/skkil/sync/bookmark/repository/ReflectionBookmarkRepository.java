package com.skkil.sync.bookmark.repository;

import com.skkil.sync.bookmark.model.ReflectionBookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReflectionBookmarkRepository extends JpaRepository<ReflectionBookmark, Long> {

  void deleteByUser_IdAndReflection_Id(Long userId, Long reflectionId);
}
