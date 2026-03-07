package com.skkil.sync.user.service;

import com.skkil.sync.user.dto.response.GetConnectionsResponse;
import com.skkil.sync.user.exception.UserCannotFollowSelfException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserFollowRelationship;
import com.skkil.sync.user.repository.UserFollowRelationshipRepository;
import com.skkil.sync.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserRelationshipService {

  private final UserRepository userRepository;
  private final UserFollowRelationshipRepository userFollowRelationshipRepository;

  public UserRelationshipService(
      UserRepository userRepository,
      UserFollowRelationshipRepository userFollowRelationshipRepository) {
    this.userRepository = userRepository;
    this.userFollowRelationshipRepository = userFollowRelationshipRepository;
  }

  @Transactional
  public void followUser(Long followerId, Long followeeId) {
    log.debug("User {} is attempting to follow user {}", followerId, followeeId);

    if (isFollowing(followerId, followeeId)) {
      log.debug("User {} is already following user {}", followerId, followeeId);
      return;
    }

    if (followerId.equals(followeeId)) {
      log.debug("Follower ID and followee ID are the same: {}", followerId);
      throw new UserCannotFollowSelfException();
    }

    User follower = userRepository.getReferenceById(followerId),
        followee = userRepository.getReferenceById(followeeId);

    var relationship =
        UserFollowRelationship.builder().follower(follower).followee(followee).build();
    userFollowRelationshipRepository.save(relationship);
  }

  @Transactional(readOnly = true)
  public GetConnectionsResponse getConnections(Long userId) {
    log.debug("Retrieving connections for user {}", userId);
    var connections = userFollowRelationshipRepository.findByFollower(new User(userId));

    return new GetConnectionsResponse(
        connections.stream()
            .map(
                rel -> {
                  var followee = rel.getFollowee();
                  var provider = followee.getCurrentProvider();
                  String providerName = (provider != null) ? provider.getName() : null;

                  return new GetConnectionsResponse.Connection(
                      followee.getId().toString(),
                      followee.getFullName(),
                      providerName,
                      followee.getProfession());
                })
            .toList());
  }

  @Transactional(readOnly = true)
  public boolean isFollowing(Long followerId, Long followeeId) {
    log.debug("Checking if user {} is following user {}", followerId, followeeId);

    if (followerId == null || followeeId == null) {
      log.debug(
          "One of the user IDs is null: followerId={}, followeeId={}", followerId, followeeId);
      return false;
    }

    boolean result =
        userFollowRelationshipRepository.existsByFollowerAndFollowee(followerId, followeeId);
    log.debug("User {} is {}following user {}", followerId, result ? "" : "not ", followeeId);

    return result;
  }

  @Transactional
  public void unfollowUser(Long followerId, Long followeeId) {
    log.debug("User {} is attempting to unfollow user {}", followerId, followeeId);
    userFollowRelationshipRepository.deleteByFollowerAndFollowee(followerId, followeeId);
  }
}
