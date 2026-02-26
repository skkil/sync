package com.skkil.sync.user.service;

import com.skkil.sync.media.constant.MediaStatus;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.MediaService;
import com.skkil.sync.user.dto.request.UpdateProfileRequest;
import com.skkil.sync.user.dto.response.GetAuthenticatedUserResponse;
import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ProfileService {

  private final UserRelationshipService userRelationshipService;
  private final UserRepository userRepository;
  private final MediaService mediaService;

  public ProfileService(
      UserRelationshipService userRelationshipService,
      UserRepository userRepository,
      MediaService mediaService) {
    this.userRelationshipService = userRelationshipService;
    this.userRepository = userRepository;
    this.mediaService = mediaService;
  }

  @Transactional(readOnly = true)
  public GetProfileResponse getProfile(Long requesterId, Long userId) {
    log.debug("User {} is requesting profile for user {}", requesterId, userId);
    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    boolean isFollowing =
        (requesterId == null || requesterId.equals(userId))
            ? false
            : userRelationshipService.isFollowing(requesterId, userId);

    return GetProfileResponse.builder()
        .userId(user.getId().toString())
        .name(user.getFullName())
        .email(user.getEmail())
        .bio(user.getBio())
        .profession(user.getProfession())
        .isFollowing(isFollowing)
        .build();
  }

  @Transactional(readOnly = true)
  public GetAuthenticatedUserResponse getAuthenticatedUser(Long userId) {
    User user = userRepository.findById(userId).orElseThrow();

    String profileImageUrl = null;
    if (user.getProfileImage() != null) {
      profileImageUrl = mediaService.getMediaUrl(user.getProfileImage().getId()).toExternalForm();
    }

    return GetAuthenticatedUserResponse.builder()
        .role(user.getRole())
        .userId(user.getId())
        .fullName(user.getFullName())
        .email(user.getEmail())
        .profileImageUrl(profileImageUrl)
        .isOnboarded(user.getIsOnboarded())
        .build();
  }

  @Transactional(readOnly = true)
  public Page<User> searchUsers(String query, int page, int size) {
    log.debug("Searching for users with query '{}', page {}, size {}", query, page, size);

    Pageable pageable = PageRequest.of(page, size);
    return userRepository.findByFullNameContainingIgnoreCase(query, pageable);
  }

  @Transactional(readOnly = true)
  public long countUsers(String query) {
    log.debug("Counting users with query '{}'", query);
    return userRepository.countByFullNameContainingIgnoreCase(query);
  }

  @Transactional
  public void updateProfile(Long userId, UpdateProfileRequest request) {
    var user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

    log.debug("Updating profile for user {}", userId);

    user.updateFields(request.name(), request.profession(), request.bio());

    if (request.profileImageId() != null) {
      if (user.getProfileImage() != null) {
        user.getProfileImage().setStatus(MediaStatus.DELETED);
      }

      Media profileImage = mediaService.getMedia(request.profileImageId());

      if (!profileImage.getStatus().equals(MediaStatus.PENDING)) {
        throw new IllegalArgumentException(
            "Media is not in a valid state to be used as profile image.");
      }

      if (!userId.equals(profileImage.getUploader().getId())) {
        throw new IllegalArgumentException(
            "User does not own the media they are trying to set as profile image.");
      }

      profileImage.setStatus(MediaStatus.UPLOADED);
      user.setProfileImage(profileImage);
    }

    if (request.isOnboarded() != null) {
      user.onboard();
    }
  }
}
