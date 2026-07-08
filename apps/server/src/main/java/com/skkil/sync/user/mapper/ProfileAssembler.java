package com.skkil.sync.user.mapper;

import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class ProfileAssembler {

  private final ProfileMapper profileMapper;

  public ProfileAssembler(ProfileMapper profileMapper) {
    this.profileMapper = profileMapper;
  }

  public GetProfileResponse toGetProfileResponse(
      User user, String profileImageUrl, boolean isFollowing, boolean isAuthenticatedUser) {
    return GetProfileResponse.builder()
        .userId(user.getId().toString())
        .handle(user.getHandle())
        .name(user.getFullName())
        .email(user.getEmail())
        .bio(user.getBio())
        .profession(user.getProfession())
        .profileImageUrl(profileImageUrl)
        .isFollowing(isFollowing)
        .followerCount(user.getFollowerCount())
        .followingCount(user.getFollowingCount())
        .isOnboarded(user.getIsOnboarded())
        .isAuthenticatedUser(isAuthenticatedUser)
        .role(user.getRole())
        .contacts(profileMapper.toGetProfileResponseContacts(user.getContacts()))
        .build();
  }
}
