package com.skkil.sync.user.service;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.common.util.pagination.service.PaginationService;
import com.skkil.sync.user.dto.response.GetConnectionsResponse;
import com.skkil.sync.user.exception.UserCannotFollowSelfException;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.mapper.UserConnectionAssembler;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserFollowRelationship;
import com.skkil.sync.user.repository.UserConnectionQueryRepository;
import com.skkil.sync.user.repository.UserFollowRelationshipRepository;
import com.skkil.sync.user.repository.UserRepository;
import com.skkil.sync.user.repository.pagination.UserConnectionCursorPaginationProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserRelationshipService {

  private final UserRepository userRepository;
  private final UserFollowRelationshipRepository userFollowRelationshipRepository;
  private final UserConnectionQueryRepository userConnectionQueryRepository;
  private final UserConnectionCursorPaginationProvider connectionPaginationProvider;
  private final UserConnectionAssembler userConnectionAssembler;
  private final PaginationService paginationService;

  public UserRelationshipService(
      UserRepository userRepository,
      UserFollowRelationshipRepository userFollowRelationshipRepository,
      UserConnectionQueryRepository userConnectionQueryRepository,
      UserConnectionCursorPaginationProvider connectionPaginationProvider,
      UserConnectionAssembler userConnectionAssembler,
      PaginationService paginationService) {
    this.userRepository = userRepository;
    this.userFollowRelationshipRepository = userFollowRelationshipRepository;
    this.userConnectionQueryRepository = userConnectionQueryRepository;
    this.connectionPaginationProvider = connectionPaginationProvider;
    this.userConnectionAssembler = userConnectionAssembler;
    this.paginationService = paginationService;
  }

  @Transactional
  public void followUser(Long followerId, Long followeeId) {
    log.debug("User {} is attempting to follow user {}", followerId, followeeId);

    if (followerId.equals(followeeId)) {
      log.debug("Follower ID and followee ID are the same: {}", followerId);
      throw new UserCannotFollowSelfException();
    }

    User followee =
        userRepository
            .findById(followeeId)
            .orElseThrow(() -> new UserNotFoundException(followeeId));

    if (userFollowRelationshipRepository.existsByFollowerAndFollowee(followerId, followeeId)) {
      log.debug("User {} is already following user {}", followerId, followeeId);
      return;
    }

    User follower = userRepository.getReferenceById(followerId);
    var relationship =
        UserFollowRelationship.builder().follower(follower).followee(followee).build();

    try {
      // saveAndFlush is required here (rather than save) so that a unique-constraint
      // violation from a concurrent follow request surfaces inside this try block
      // instead of at transaction commit, after the method has already returned.
      userFollowRelationshipRepository.saveAndFlush(relationship);
    } catch (DataIntegrityViolationException e) {
      log.debug(
          "User {} was concurrently followed to user {}, ignoring duplicate",
          followerId,
          followeeId);
      return;
    }

    userRepository.incrementFollowerCount(followeeId);
    userRepository.incrementFollowingCount(followerId);
  }

  @Transactional(readOnly = true)
  public boolean isFollowing(Long followerId, Long followeeId) {
    log.debug("Checking if user {} is following user {}", followerId, followeeId);

    if (followerId == null || followeeId == null) {
      log.debug(
          "One of the user IDs is null: followerId={}, followeeId={}", followerId, followeeId);
      return false;
    }

    if (followerId.equals(followeeId)) {
      log.debug("Follower ID and followee ID are the same: {}", followerId);
      return false;
    }

    boolean result =
        userFollowRelationshipRepository.existsByFollowerAndFollowee(followerId, followeeId);
    log.debug("User {} is {}following user {}", followerId, result ? "" : "not ", followeeId);

    return result;
  }

  @Transactional(readOnly = true)
  public GetConnectionsResponse getFollowing(Long userId, CursorPaginationRequest pagination) {
    log.debug("Retrieving users followed by user {}", userId);

    var connections =
        paginationService.paginate(
            userConnectionQueryRepository.getFollowing(userId),
            connectionPaginationProvider,
            pagination);

    return userConnectionAssembler.toGetConnectionsResponse(connections);
  }

  @Transactional(readOnly = true)
  public GetConnectionsResponse getFollowers(Long userId, CursorPaginationRequest pagination) {
    log.debug("Retrieving followers of user {}", userId);

    var connections =
        paginationService.paginate(
            userConnectionQueryRepository.getFollowers(userId),
            connectionPaginationProvider,
            pagination);

    return userConnectionAssembler.toGetConnectionsResponse(connections);
  }

  @Transactional
  public void unfollowUser(Long followerId, Long followeeId) {
    log.debug("User {} is attempting to unfollow user {}", followerId, followeeId);

    int deleted =
        userFollowRelationshipRepository.deleteByFollowerAndFollowee(followerId, followeeId);
    if (deleted == 0) {
      return;
    }

    userRepository.decrementFollowerCount(followeeId);
    userRepository.decrementFollowingCount(followerId);
  }
}
