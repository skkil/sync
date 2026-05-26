package com.skkil.sync.reflection.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.reflection.dto.request.CreateReflectionRequest;
import com.skkil.sync.reflection.dto.request.UpdateReflectionRequest;
import com.skkil.sync.reflection.dto.request.UpdateReflectionSummaryRequest;
import com.skkil.sync.reflection.dto.response.CreateReflectionResponse;
import com.skkil.sync.reflection.service.ReflectionService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReflectionController {

  private final ReflectionService reflectionService;

  public ReflectionController(ReflectionService reflectionService) {
    this.reflectionService = reflectionService;
  }

  @PostMapping("/reflections")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateReflectionResponse createReflection(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated CreateReflectionRequest request) {
    return reflectionService.createReflection(user.userId(), request);
  }

  @PatchMapping("/reflections/{reflectionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateReflection(
      @PathVariable Long reflectionId, @RequestBody @Validated UpdateReflectionRequest request) {
    reflectionService.updateReflection(reflectionId, request);
  }

  @PatchMapping("/reflections/{reflectionId}/summary")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateReflectionSummary(
      @PathVariable Long reflectionId,
      @RequestBody @Validated UpdateReflectionSummaryRequest request) {
    reflectionService.updateReflectionSummary(reflectionId, request);
  }

  @DeleteMapping("/reflections/{reflectionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteReflection(@PathVariable Long reflectionId) {
    reflectionService.deleteReflection(reflectionId);
  }
}
