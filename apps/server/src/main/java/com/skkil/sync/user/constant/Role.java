package com.skkil.sync.user.constant;

public enum Role {
  ADMIN("ADMIN"),
  USER("USER");

  private final String name;

  Role(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
