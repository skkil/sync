package com.skkil.sync.reflection.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.reflection.dto.response.GetReflectionResponse;
import com.skkil.sync.reflection.dto.response.GetReflectionsResponse;
import com.skkil.sync.reflection.service.ReflectionQueryService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReflectionQueryController {

  private final ReflectionQueryService reflectionQueryService;

  public ReflectionQueryController(ReflectionQueryService reflectionService) {
    this.reflectionQueryService = reflectionService;
  }

  @GetMapping("/reflections")
  @ResponseStatus(HttpStatus.OK)
  public GetReflectionsResponse getReflections(@Validated CursorPaginationRequest pagination) {
    return reflectionQueryService.getReflections(pagination);
  }

  @GetMapping("/reflections/{slug}")
  @ResponseStatus(HttpStatus.OK)
  public GetReflectionResponse getReflectionBySlug(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable String slug) {
    return reflectionQueryService.getReflectionBySlug(user == null ? null : user.userId(), slug);
  }

  @GetMapping("/users/{userId}/reflections")
  @ResponseStatus(HttpStatus.OK)
  public GetReflectionsResponse getUserReflections(
      @PathVariable Long userId, @Validated CursorPaginationRequest pagination) {
    return reflectionQueryService.getUserReflections(userId, pagination);
  }

  @GetMapping("/projects/{handle}/reflections")
  @ResponseStatus(HttpStatus.OK)
  public GetReflectionsResponse getReflectionsByProject(
      @PathVariable String handle, @Validated CursorPaginationRequest pagination) {
    return reflectionQueryService.getReflectionsByProject(handle, pagination);
  }
}
