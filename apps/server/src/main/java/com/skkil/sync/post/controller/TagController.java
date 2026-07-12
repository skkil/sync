package com.skkil.sync.post.controller;

import com.skkil.sync.post.dto.request.CreateTagRequest;
import com.skkil.sync.post.dto.response.CreateTagResponse;
import com.skkil.sync.post.dto.response.GetTagsResponse;
import com.skkil.sync.post.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
  public GetTagsResponse searchTags(
      @RequestParam(required = false) String handle, @RequestParam(required = true) String query) {
    return tagService.searchTags(handle, query);
  }

  @GetMapping("/projects/{handle}/tags")
  @ResponseStatus(HttpStatus.OK)
  public GetTagsResponse getProjectTags(@PathVariable String handle) {
    return tagService.getProjectTags(handle);
  }

  @GetMapping("/tags/unverified")
  @ResponseStatus(HttpStatus.OK)
  public GetTagsResponse getUnverifiedTags() {
    return tagService.getUnverifiedTags();
  }

  @GetMapping("/projects/{handle}/tags/unverified")
  @ResponseStatus(HttpStatus.OK)
  public GetTagsResponse getProjectUnverifiedTags(@PathVariable String handle) {
    return tagService.getProjectUnverifiedTags(handle);
  }

  @PatchMapping("/tags/{tagId}/verify")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void verifyTag(@PathVariable Long tagId) {
    tagService.verifyTag(tagId);
  }

  @PostMapping("/tags")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateTagResponse createTag(@RequestBody @Validated CreateTagRequest request) {
    return tagService.createTag(request);
  }

  @PostMapping("/projects/{handle}/tags")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateTagResponse createProjectTag(
      @PathVariable String handle, @RequestBody @Validated CreateTagRequest request) {
    return tagService.createProjectTag(handle, request);
  }
}
