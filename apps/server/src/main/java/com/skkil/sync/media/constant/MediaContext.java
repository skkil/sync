package com.skkil.sync.media.constant;

public enum MediaContext {
  PROFILE_IMAGE("profile-image");

  private final String name;

  MediaContext(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return name;
  }
}
