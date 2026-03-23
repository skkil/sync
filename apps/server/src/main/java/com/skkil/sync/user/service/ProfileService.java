package com.skkil.sync.user.service;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.media.constant.MediaStatus;
import com.skkil.sync.media.model.Media;
import com.skkil.sync.media.service.MediaService;
import com.skkil.sync.user.dto.request.UpdateProfileRequest;
import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.exception.UserNotFoundException;
import com.skkil.sync.user.mapper.ProfileMapper;
import com.skkil.sync.user.model.User;
import com.skkil.sync.user.model.UserContacts;
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
  private final ProfileMapper profileMapper;

  public ProfileService(
      UserRelationshipService userRelationshipService,
      UserRepository userRepository,
      MediaService mediaService,
      ProfileMapper profileMapper) {
    this.userRelationshipService = userRelationshipService;
    this.userRepository = userRepository;
    this.mediaService = mediaService;
    this.profileMapper = profileMapper;
  }

  @Transactional(readOnly = true)
  public GetProfileResponse getProfileByHandle(AuthenticatedUser requester, String handle) {
    User user =
        userRepository.findByHandle(handle).orElseThrow(() -> new UserNotFoundException(handle));

    return getProfileById(requester, user.getId());
  }

  @Transactional(readOnly = true)
  public GetProfileResponse getProfileById(AuthenticatedUser requester, Long userId) {
    Long requesterId = requester != null ? requester.userId() : null;
    log.debug("User {} is requesting profile for user {}", requesterId, userId);

    User user =
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

    boolean isAuthenticatedUser = requesterId != null && requesterId.equals(userId);
    if (!user.isEnabled() && !isAuthenticatedUser) {
      log.debug("User {} is trying to access disabled profile of user {}", requesterId, userId);
      throw new UserNotFoundException(userId);
    }

    String profileImageUrl = null;
    if (user.getProfileImage() != null) {
      log.debug(
          "User {} has profile image with media ID {}, generating URL",
          userId,
          user.getProfileImage().getId());
      profileImageUrl = mediaService.getMediaUrl(user.getProfileImage().getId()).toExternalForm();
    }

    boolean isFollowing = userRelationshipService.isFollowing(requesterId, userId);

    return GetProfileResponse.builder()
        .userId(user.getId().toString())
        .handle(user.getHandle())
        .name(user.getFullName())
        .email(user.getEmail())
        .bio(user.getBio())
        .profession(user.getProfession())
        .profileImageUrl(profileImageUrl)
        .isFollowing(isFollowing)
        .isOnboarded(user.getIsOnboarded())
        .isAuthenticatedUser(isAuthenticatedUser)
        .role(user.getRole())
        .contacts(profileMapper.toGetProfileResponseContacts(user.getContacts()))
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

    if (request.handle() != null) {
      user.updateHandle(request.handle());
    }

    if (Boolean.TRUE.equals(request.removeProfileImage()) && user.getProfileImage() != null) {
      user.getProfileImage().setStatus(MediaStatus.DELETED);
      user.setProfileImage(null);
    }

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

    if (request.contacts() != null) {
      UserContacts contacts = profileMapper.toUserContacts(request.contacts());
      user.setContacts(contacts);
    }
  }
}
