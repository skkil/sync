package com.skkil.sync.post.controller;

import com.skkil.sync.post.dto.response.SearchTagsResponse;
import com.skkil.sync.post.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TagController {

  private final TagService tagService;

  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  @GetMapping("/search/tags")
  @ResponseStatus(HttpStatus.OK)
  public SearchTagsResponse searchTags(@RequestParam(required = true) String query) {
    return tagService.searchTags(query);
  }
}
