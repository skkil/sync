package com.skkil.sync.bookmark.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.bookmark.dto.response.GetBookmarkedReflectionsResponse;
import com.skkil.sync.bookmark.service.ReflectionBookmarkQueryService;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReflectionBookmarkQueryController {

  private final ReflectionBookmarkQueryService reflectionBookmarkQueryService;

  public ReflectionBookmarkQueryController(
      ReflectionBookmarkQueryService reflectionBookmarkQueryService) {
    this.reflectionBookmarkQueryService = reflectionBookmarkQueryService;
  }

  @GetMapping("/bookmarks/reflections")
  @ResponseStatus(HttpStatus.OK)
  public GetBookmarkedReflectionsResponse getBookmarkedReflections(
      @AuthenticationPrincipal AuthenticatedUser user,
      @Validated CursorPaginationRequest pagination) {
    return reflectionBookmarkQueryService.getBookmarkedReflections(user.userId(), pagination);
  }
}
