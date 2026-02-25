package com.skkil.sync.user.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserContacts implements Serializable {

  private String custom;

  private String linkedin;

  private String github;

  private String instagram;

  private String twitter;

  @Override
  public String toString() {
    return String.format(
        "UserContacts{custom='%s', linkedin='%s', github='%s', instagram='%s', twitter='%s'}",
        custom, linkedin, github, instagram, twitter);
  }
}
