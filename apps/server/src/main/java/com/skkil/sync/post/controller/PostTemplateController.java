package com.skkil.sync.post.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.post.dto.request.CreatePostTemplateRequest;
import com.skkil.sync.post.dto.request.UpdatePostTemplateRequest;
import com.skkil.sync.post.dto.response.CreatePostTemplateResponse;
import com.skkil.sync.post.dto.response.GetPostTemplatesResponse;
import com.skkil.sync.post.service.PostTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostTemplateController {

  private final PostTemplateService templateService;

  public PostTemplateController(PostTemplateService templateService) {
    this.templateService = templateService;
  }

  @GetMapping("/projects/{handle}/post-templates")
  public GetPostTemplatesResponse getProjectTemplates(@PathVariable String handle) {
    return templateService.getProjectTemplates(handle);
  }

  @PostMapping("/projects/{handle}/post-templates")
  @ResponseStatus(HttpStatus.CREATED)
  public CreatePostTemplateResponse createTemplate(
      @AuthenticationPrincipal AuthenticatedUser user,
      @PathVariable String handle,
      @RequestBody @Validated CreatePostTemplateRequest request) {
    return templateService.createTemplate(user.userId(), handle, request);
  }

  @PatchMapping("/projects/{handle}/post-templates/{externalId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateTemplate(
      @PathVariable String handle,
      @PathVariable String externalId,
      @RequestBody @Validated UpdatePostTemplateRequest request) {
    templateService.updateTemplate(handle, externalId, request);
  }

  @DeleteMapping("/projects/{handle}/post-templates/{externalId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteTemplate(@PathVariable String handle, @PathVariable String externalId) {
    templateService.deleteTemplate(handle, externalId);
  }
}
