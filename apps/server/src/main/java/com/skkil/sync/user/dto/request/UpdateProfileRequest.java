package com.skkil.sync.user.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(min = 1, max = 255) String name,
    Long profileImageId,
    Boolean isOnboarded,
    @Size(max = 1000) String bio,
    @Size(max = 255) String profession,
    Contacts contacts) {

  public static record Contacts(
      @Size(max = 255) String custom,
      @Size(max = 255) String linkedin,
      @Size(max = 255) String github,
      @Size(max = 255) String instagram,
      @Size(max = 255) String twitter) {}
}
