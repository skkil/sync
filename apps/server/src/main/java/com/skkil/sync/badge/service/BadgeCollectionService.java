package com.skkil.sync.badge.service;

import com.skkil.sync.badge.model.BadgeCollection;
import com.skkil.sync.badge.repository.BadgeCollectionRepository;
import com.skkil.sync.reflection.model.Tag;
import com.skkil.sync.user.model.User;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BadgeCollectionService {

  private static final long LEVEL_4_POST_COUNT = 20L;
  private static final long LEVEL_3_POST_COUNT = 10L;
  private static final long LEVEL_2_POST_COUNT = 5L;
  private static final long LEVEL_1_POST_COUNT = 1L;

  private final BadgeCollectionRepository badgeCollectionRepository;

  public BadgeCollectionService(BadgeCollectionRepository badgeCollectionRepository) {
    this.badgeCollectionRepository = badgeCollectionRepository;
  }

  @Transactional
  public void increaseBadges(User user, List<Tag> tags) {
    for (Tag tag : tags) {
      if (!tag.isVerified()) {
        continue;
      }

      BadgeCollection badgeCollection =
          badgeCollectionRepository
              .findByUserIdAndTagId(user.getId(), tag.getId())
              .orElseGet(() -> new BadgeCollection(user, tag, 0L, 0));

      long postCount = badgeCollection.getPostCount() + 1;
      badgeCollection.updateProgress(postCount, calculateLevel(postCount));
      badgeCollectionRepository.save(badgeCollection);
    }
  }

  @Transactional
  public void decreaseBadges(User user, List<Tag> tags) {
    for (Tag tag : tags) {
      if (!tag.isVerified()) {
        continue;
      }

      badgeCollectionRepository
          .findByUserIdAndTagId(user.getId(), tag.getId())
          .ifPresent(
              badgeCollection -> {
                long postCount = badgeCollection.getPostCount() - 1;
                if (postCount <= 0) {
                  badgeCollectionRepository.delete(badgeCollection);
                  return;
                }

                badgeCollection.updateProgress(postCount, calculateLevel(postCount));
              });
    }
  }

  private int calculateLevel(long postCount) {
    if (postCount >= LEVEL_4_POST_COUNT) {
      return 4;
    }
    if (postCount >= LEVEL_3_POST_COUNT) {
      return 3;
    }
    if (postCount >= LEVEL_2_POST_COUNT) {
      return 2;
    }
    if (postCount >= LEVEL_1_POST_COUNT) {
      return 1;
    }

    return 0;
  }
}
