package com.skkil.sync.media.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.media.dto.request.UploadMediaRequest;
import com.skkil.sync.media.dto.response.UploadMediaResponse;
import com.skkil.sync.media.service.MediaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediaController {

  private final MediaService mediaService;

  public MediaController(MediaService mediaService) {
    this.mediaService = mediaService;
  }

  @PostMapping("/media")
  @ResponseStatus(HttpStatus.CREATED)
  public UploadMediaResponse uploadMedia(
      @AuthenticationPrincipal AuthenticatedUser user,
      @RequestBody @Validated UploadMediaRequest request) {
    return mediaService.uploadMedia(user.userId(), request);
  }
}
