package com.skkil.sync.experience.controller;

import com.skkil.sync.experience.dto.request.UpdateReflectionRequest;
import com.skkil.sync.experience.dto.request.CreateReflectionRequest;
import com.skkil.sync.experience.dto.response.GetReflectionsResponse;
import com.skkil.sync.experience.service.ReflectionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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

  @PostMapping("/experiences/{experienceId}/reflections")
  public void createReflection(
      @PathVariable Long experienceId, @RequestBody @Validated CreateReflectionRequest request) {
    reflectionService.createReflection(experienceId, request);
  }

  @GetMapping("/experiences/{experienceId}/reflections")
  public GetReflectionsResponse getReflections(@PathVariable Long experienceId) {
    return reflectionService.getReflections(experienceId);
  }

  @PatchMapping("/experiences/{experienceId}/reflections/{reflectionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateReflection(
      @PathVariable Long experienceId,
      @PathVariable Long reflectionId,
      @RequestBody @Validated UpdateReflectionRequest request) {
    reflectionService.updateReflection(experienceId, reflectionId, request);
  }

  @DeleteMapping("/experiences/{experienceId}/reflections/{reflectionId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteReflection(
      @PathVariable Long experienceId, @PathVariable Long reflectionId) {
    reflectionService.deleteReflection(experienceId, reflectionId);
  }
}
