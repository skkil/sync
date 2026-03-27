package com.skkil.sync.user.dto.response;

import com.skkil.sync.user.constant.Role;
import lombok.Builder;

@Builder
public record GetProfileResponse(
    String userId,
    String handle,
    String name,
    String email,
    String bio,
    String profession,
    String profileImageUrl,
    boolean isFollowing,
    boolean isOnboarded,
    boolean isAuthenticatedUser,
    Role role,
    Contacts contacts) {

  public static record Contacts(
      String custom, String linkedin, String github, String instagram, String twitter) {}
}
