package com.skkil.sync.user.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Theme {
  LIGHT("light"),
  DARK("dark"),
  SYSTEM("system");

  private final String value;

  Theme(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }
}
