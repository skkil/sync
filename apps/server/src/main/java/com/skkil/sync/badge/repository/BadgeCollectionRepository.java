package com.skkil.sync.badge.repository;

import com.skkil.sync.badge.model.BadgeCollection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeCollectionRepository extends JpaRepository<BadgeCollection, Long> {

  Optional<BadgeCollection> findByUserIdAndTagId(Long userId, Long tagId);

  @EntityGraph(attributePaths = {"tag"})
  List<BadgeCollection> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
