package com.skkil.sync.media.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaContext {
  PROFILE_IMAGE("profile-image");

  private final String name;

  MediaContext(String name) {
    this.name = name;
  }

  @JsonCreator
  public static MediaContext fromValue(String value) {
    for (MediaContext context : values()) {
      if (context.name.equalsIgnoreCase(value) || context.name().equalsIgnoreCase(value)) {
        return context;
      }
    }
    throw new IllegalArgumentException("Unknown media context: " + value);
  }

  @JsonValue
  @Override
  public String toString() {
    return name;
  }
}
