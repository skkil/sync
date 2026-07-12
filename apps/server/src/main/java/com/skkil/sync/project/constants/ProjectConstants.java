package com.skkil.sync.project.constants;

import java.time.Duration;
import java.util.Set;

public class ProjectConstants {

  public static final int MIN_HANDLE_LENGTH = 6;
  public static final int MAX_HANDLE_LENGTH = 30;

  public static final Set<String> RESERVED_HANDLES =
      Set.of(
          "new",
          "invitations",
          "handles",
          "settings",
          "admin",
          "api",
          "auth",
          "login",
          "register",
          "onboarding",
          "about",
          "terms",
          "privacy",
          "explore",
          "search",
          "messages",
          "bookmarks",
          "cookies",
          "support",
          "help");

  public static final int MIN_NAME_LENGTH = 6;
  public static final int MAX_NAME_LENGTH = 50;

  public static final int MAX_DESCRIPTION_LENGTH = 500;

  public static final int INITIAL_TEAMMATE_LOAD_LIMIT = 5;

  public static final Duration PROJECT_INVITATION_TTL = Duration.ofDays(7);
}
