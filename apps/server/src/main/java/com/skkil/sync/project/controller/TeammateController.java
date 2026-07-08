package com.skkil.sync.project.controller;

import com.skkil.sync.project.dto.request.AddTeammateRequest;
import com.skkil.sync.project.dto.request.UpdateTeammateRequest;
import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.service.TeammateService;
import org.springframework.http.HttpStatus;
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
@Validated
public class TeammateController {

  private final TeammateService teammateService;

  public TeammateController(TeammateService teammateService) {
    this.teammateService = teammateService;
  }

  @GetMapping("/projects/{handle}/teammates")
  public GetProjectTeammatesResponse getProjectTeammates(@PathVariable String handle) {
    return teammateService.getProjectTeammates(handle);
  }

  @PostMapping("/projects/{handle}/teammates")
  @ResponseStatus(HttpStatus.CREATED)
  public void addTeammate(
      @PathVariable String handle, @RequestBody @Validated AddTeammateRequest request) {
    teammateService.addTeammate(handle, request);
  }

  @DeleteMapping("/projects/{handle}/teammates/{teammateHandle}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeTeammate(@PathVariable String handle, @PathVariable String teammateHandle) {
    teammateService.removeTeammate(handle, teammateHandle);
  }

  @PatchMapping("/projects/{handle}/teammates/{teammateHandle}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateTeammate(
      @PathVariable String handle,
      @PathVariable String teammateHandle,
      @RequestBody @Validated UpdateTeammateRequest request) {
    teammateService.updateTeammate(handle, teammateHandle, request);
  }
}
