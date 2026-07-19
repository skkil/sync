package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.common.util.pagination.dto.request.OffsetPaginationRequest;
import com.skkil.sync.post.dto.request.CreateTagRequest;
import com.skkil.sync.post.dto.request.MergeTagsRequest;
import com.skkil.sync.post.dto.request.UpdateTagRequest;
import com.skkil.sync.post.dto.response.CreateTagResponse;
import com.skkil.sync.post.dto.response.GetAllTagsResponse;
import com.skkil.sync.post.dto.response.GetTagResponse;
import com.skkil.sync.post.dto.response.GetTagsResponse;
import com.skkil.sync.post.service.TagService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class TagController {

  private final TagService tagService;

  public TagController(TagService tagService) {
    this.tagService = tagService;
  }

  @GetMapping("/search/tags")
  @ResponseStatus(HttpStatus.OK)
  public GetTagsResponse searchTags(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestParam(required = false) String handle,
      @RequestParam @NotBlank @Size(min = 1, max = 100) String query) {
    return tagService.searchTags(user.userId(), handle, query);
  }

  @GetMapping("/tags/{id}")
  @ResponseStatus(HttpStatus.OK)
  public GetTagResponse getTag(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable Long id) {
    return new GetTagResponse(tagService.getTag(user == null ? null : user.userId(), id));
  }

  @GetMapping("/tags")
  @ResponseStatus(HttpStatus.OK)
  public GetAllTagsResponse getAllTags(
      @AuthenticationPrincipal AuthenticatedUser user,
      @Validated OffsetPaginationRequest pagination) {
    return tagService.getAllTags(user.userId(), pagination);
  }

  @GetMapping("/projects/{handle}/tags")
  @ResponseStatus(HttpStatus.OK)
  public GetTagsResponse getProjectTags(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable String handle) {
    return tagService.getProjectTags(user.userId(), handle);
  }

  @GetMapping("/tags/unverified")
  @ResponseStatus(HttpStatus.OK)
  public GetTagsResponse getUnverifiedTags(@AuthenticationPrincipal AuthenticatedUser user) {
    return tagService.getUnverifiedTags(user.userId());
  }

  @GetMapping("/projects/{handle}/tags/unverified")
  @ResponseStatus(HttpStatus.OK)
  public GetTagsResponse getProjectUnverifiedTags(
      @AuthenticationPrincipal AuthenticatedUser user, @PathVariable String handle) {
    return tagService.getProjectUnverifiedTags(user.userId(), handle);
  }

  @PatchMapping("/tags/{name}/verify")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void verifyTag(@PathVariable String name) {
    tagService.verifyTag(name);
  }

  @PatchMapping("/projects/{handle}/tags/{name}/verify")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void verifyProjectTag(@PathVariable String handle, @PathVariable String name) {
    tagService.verifyProjectTag(handle, name);
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

  @PatchMapping("/tags/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateTag(
      @PathVariable String name, @RequestBody @Validated UpdateTagRequest request) {
    tagService.updateTag(name, request);
  }

  @PatchMapping("/projects/{handle}/tags/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateProjectTag(
      @PathVariable String handle,
      @PathVariable String name,
      @RequestBody @Validated UpdateTagRequest request) {
    tagService.updateProjectTag(handle, name, request);
  }

  @DeleteMapping("/tags/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void rejectTag(@PathVariable String name) {
    tagService.rejectTag(name);
  }

  @DeleteMapping("/projects/{handle}/tags/{name}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void rejectProjectTag(@PathVariable String handle, @PathVariable String name) {
    tagService.rejectProjectTag(handle, name);
  }

  @PostMapping("/tags/merge")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void mergeTags(@RequestBody @Validated MergeTagsRequest request) {
    tagService.mergeTags(request);
  }
}
