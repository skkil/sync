package com.skkil.sync.media.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaContext {
  PROFILE_IMAGE("profile-image");

  private final String name;

  MediaContext(String name) {
    this.name = name;
  }

  @JsonValue
  @Override
  public String toString() {
    return name;
  }
}
