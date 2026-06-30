package com.skkil.sync.post.controller;

import com.skkil.sync.post.dto.response.SearchPostsResponse;
import com.skkil.sync.post.service.PostSearchService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class PostSearchController {

  private final PostSearchService postSearchService;

  public PostSearchController(PostSearchService postSearchService) {
    this.postSearchService = postSearchService;
  }

  @GetMapping("/search/posts")
  @ResponseStatus(HttpStatus.OK)
  public SearchPostsResponse searchPosts(
      @RequestParam @NotBlank @Size(min = 1, max = 100) String query) {
    return postSearchService.searchPosts(query);
  }
}
