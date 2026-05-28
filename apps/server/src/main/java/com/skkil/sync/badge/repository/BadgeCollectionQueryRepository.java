package com.skkil.sync.badge.repository;

import com.skkil.sync.badge.model.BadgeCollection;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class BadgeCollectionQueryRepository {

  private final BadgeCollectionRepository badgeCollectionRepository;

  public BadgeCollectionQueryRepository(BadgeCollectionRepository badgeCollectionRepository) {
    this.badgeCollectionRepository = badgeCollectionRepository;
  }

  public List<BadgeCollection> findAllByUserId(Long userId) {
    return badgeCollectionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
  }
}
