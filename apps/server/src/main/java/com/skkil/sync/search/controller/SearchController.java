package com.skkil.sync.search.controller;

import com.skkil.sync.search.dto.response.SearchResponse;
import com.skkil.sync.search.enums.SearchType;
import com.skkil.sync.search.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

  private final SearchService searchService;

  public SearchController(SearchService searchService) {
    this.searchService = searchService;
  }

  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public SearchResponse search(
      @RequestParam(required = true) String query,
      @RequestParam(required = true) SearchType type,
      @RequestParam(required = false, defaultValue = "0") int page,
      @RequestParam(required = false, defaultValue = "50") int size) {
    return searchService.search(query, type, page, size);
  }
}
