package com.skkil.sync.user.dto.response;

import lombok.Builder;

@Builder
public record GetProfileResponse(
    String userId,
    String name,
    String email,
    String bio,
    String profession,
    boolean isFollowing,
    Contacts contacts) {

  public static record Contacts(
      String custom, String linkedin, String github, String instagram, String twitter) {}
}
