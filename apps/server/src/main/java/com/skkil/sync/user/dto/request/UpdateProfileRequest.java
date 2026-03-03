package com.skkil.sync.user.dto.request;

import com.skkil.sync.common.util.validator.ValidUrl;
import com.skkil.sync.common.util.validator.ValidUsername;
import com.skkil.sync.user.constant.Handle;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
    @Size(min = 1, max = 255) String name,
    @ValidUsername @Size(min = Handle.MIN_LENGTH, max = Handle.MAX_LENGTH) String handle,
    Long profileImageId,
    Boolean removeProfileImage,
    Boolean isOnboarded,
    @Size(max = 1000) String bio,
    @Size(max = 255) String profession,
    @Valid Contacts contacts) {

  public static record Contacts(
      @ValidUrl @Size(max = 255) String custom,
      @ValidUsername @Size(max = 255) String linkedin,
      @ValidUsername @Size(max = 255) String github,
      @ValidUsername @Size(max = 255) String instagram,
      @ValidUsername @Size(max = 255) String twitter) {}
}
