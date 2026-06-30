package com.skkil.sync.post.controller;

import com.skkil.sync.post.dto.response.GetPostActivitiesResponse;
import com.skkil.sync.post.service.PostActivityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostActivityController {

  private final PostActivityService postActivityService;

  public PostActivityController(PostActivityService postActivityService) {
    this.postActivityService = postActivityService;
  }

  @GetMapping("/profiles/{handle}/posts/activities")
  public GetPostActivitiesResponse getPostActivities(
      @PathVariable String handle, @RequestParam(required = true) Integer year) {
    return postActivityService.getPostActivities(handle, year);
  }
}
