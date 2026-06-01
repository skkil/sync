package com.skkil.sync.badge.service;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.badge.dto.response.GetBadgeCollectionsResponse;
import com.skkil.sync.badge.model.BadgeCollection;
import com.skkil.sync.badge.repository.BadgeCollectionQueryRepository;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BadgeCollectionQueryService {

  private final BadgeCollectionQueryRepository badgeCollectionQueryRepository;
  private final UserRepository userRepository;

  public BadgeCollectionQueryService(
      BadgeCollectionQueryRepository badgeCollectionQueryRepository,
      UserRepository userRepository) {
    this.badgeCollectionQueryRepository = badgeCollectionQueryRepository;
    this.userRepository = userRepository;
  }

  @Transactional(readOnly = true)
  public GetBadgeCollectionsResponse getBadgeCollections(AuthenticatedUser requester, Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    Long requesterId = requester == null ? null : requester.userId();

    if (!user.isEnabled() && !userId.equals(requesterId)) {
      throw new UserNotFoundException(userId);
    }

    var badges =
        badgeCollectionQueryRepository.findAllByUserId(userId).stream()
            .map(this::toBadgeResponse)
            .toList();

    return new GetBadgeCollectionsResponse(badges);
  }

  private GetBadgeCollectionsResponse.Badge toBadgeResponse(BadgeCollection badgeCollection) {
    var tag = badgeCollection.getTag();

    return GetBadgeCollectionsResponse.Badge.builder()
        .id(badgeCollection.getId())
        .tag(
            GetBadgeCollectionsResponse.Tag.builder()
                .id(tag.getId())
                .name(tag.getName())
                .description(tag.getDescription())
                .imageUrl(tag.getImageUrl())
                .build())
        .postCount(badgeCollection.getPostCount())
        .level(badgeCollection.getLevel())
        .createdAt(badgeCollection.getCreatedAt())
        .build();
  }
}
