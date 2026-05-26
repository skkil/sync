package com.skkil.sync.reflection.controller;

import com.skkil.sync.reflection.dto.response.SearchReflectionsResponse;
import com.skkil.sync.reflection.service.ReflectionSearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class ReflectionSearchController {

  private final ReflectionSearchService reflectionSearchService;

  public ReflectionSearchController(ReflectionSearchService reflectionSearchService) {
    this.reflectionSearchService = reflectionSearchService;
  }

  @GetMapping("/search/reflections")
  public SearchReflectionsResponse searchReflections(
      @RequestParam @NotBlank @Size(min = 1, max = 100) String query) {
    return reflectionSearchService.searchReflections(query);
  }
}
