package com.skkil.sync.bookmark.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.bookmark.service.ReflectionBookmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReflectionBookmarkController {

  private final ReflectionBookmarkService reflectionBookmarkService;

  public ReflectionBookmarkController(ReflectionBookmarkService reflectionBookmarkService) {
    this.reflectionBookmarkService = reflectionBookmarkService;
  }

  @PostMapping("/reflections/{reflectionId}/bookmarks")
  @ResponseStatus(HttpStatus.OK)
  public void bookmarkReflection(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long reflectionId) {
    reflectionBookmarkService.bookmarkReflection(user.userId(), reflectionId);
  }

  @DeleteMapping("/reflections/{reflectionId}/bookmarks")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void unbookmarkReflection(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long reflectionId) {
    reflectionBookmarkService.unbookmarkReflection(user.userId(), reflectionId);
  }
}
