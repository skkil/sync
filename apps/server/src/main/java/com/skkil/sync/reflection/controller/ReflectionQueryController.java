package com.skkil.sync.reflection.controller;

import com.skkil.sync.common.util.pagination.dto.request.CursorPaginationRequest;
import com.skkil.sync.reflection.dto.response.GetReflectionsResponse;
import com.skkil.sync.reflection.service.ReflectionQueryService;
import org.springframework.http.HttpStatus;
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

  @GetMapping("/users/{userId}/reflections")
  @ResponseStatus(HttpStatus.OK)
  public GetReflectionsResponse getUserReflections(
      @PathVariable Long userId, @Validated CursorPaginationRequest pagination) {
    return reflectionQueryService.getUserReflections(userId, pagination);
  }

  @GetMapping("/experiences/project/{experienceId}/reflections")
  @ResponseStatus(HttpStatus.OK)
  public GetReflectionsResponse getProjectExperienceReflections(
      @PathVariable Long experienceId, @Validated CursorPaginationRequest pagination) {
    return reflectionQueryService.getProjectExperienceReflections(experienceId, pagination);
  }
}
