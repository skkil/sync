package com.skkil.sync.reflection.controller;

import com.skkil.sync.reflection.dto.request.UpdateTagRequest;
import com.skkil.sync.reflection.dto.response.GetTagsResponse;
import com.skkil.sync.reflection.service.TagService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class TagAdminController {

  private final TagService tagService;

  public TagAdminController(TagService tagService) {
    this.tagService = tagService;
  }

  @GetMapping("/admin/tags")
  @ResponseStatus(HttpStatus.OK)
  public GetTagsResponse getAllTags() {
    return tagService.getAllTags();
  }

  @PatchMapping("/admin/tags/{tagId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateTag(@PathVariable Long tagId, @RequestBody UpdateTagRequest request) {
    tagService.updateTag(tagId, request);
  }

  @DeleteMapping("/admin/tags/{tagId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTag(@PathVariable Long tagId) {
    tagService.deleteTag(tagId);
  }
}
