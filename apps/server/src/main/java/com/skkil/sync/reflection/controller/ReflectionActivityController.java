package com.skkil.sync.reflection.controller;

import com.skkil.sync.reflection.dto.response.GetReflectionActivitiesResponse;
import com.skkil.sync.reflection.service.ReflectionActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReflectionActivityController {

  private final ReflectionActivityService reflectionActivityService;

  public ReflectionActivityController(ReflectionActivityService reflectionActivityService) {
    this.reflectionActivityService = reflectionActivityService;
  }

  @GetMapping("/profiles/{handle}/reflections/activities")
  public GetReflectionActivitiesResponse getReflectionActivities(
      @PathVariable String handle, @RequestParam(required = true) Integer year) {
    return reflectionActivityService.getReflectionActivities(handle, year);
  }
}
