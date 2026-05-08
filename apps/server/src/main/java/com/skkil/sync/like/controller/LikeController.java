package com.skkil.sync.like.controller;

import com.skkil.sync.auth.AuthenticatedUser;
import com.skkil.sync.auth.exception.LoginRequiredException;
import com.skkil.sync.like.dto.request.CreateLikeRequest;
import com.skkil.sync.like.dto.response.CreateLikeResponse;
import com.skkil.sync.like.service.LikeService;
import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LikeController {

  private final LikeService likeService;

  public LikeController(LikeService likeService) {
    this.likeService = likeService;
  }

  @PostMapping("/likes")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateLikeResponse createLike(
      @AuthenticationPrincipal @Nullable AuthenticatedUser user,
      @RequestBody @Validated CreateLikeRequest request) {
    if (user == null) {
      throw new LoginRequiredException();
    }

    try {
      return likeService.like(user.userId(), request);
    } catch (DataIntegrityViolationException e) {
      if (likeService.hasLiked(user.userId(), request)) {
        return likeService.getLikedResponse(request);
      }
      throw e;
    }
  }

  @DeleteMapping("/likes")
  @ResponseStatus(HttpStatus.OK)
  public CreateLikeResponse deleteLike(
      @AuthenticationPrincipal @Nullable AuthenticatedUser user,
      @RequestBody @Validated CreateLikeRequest request) {
    if (user == null) {
      throw new LoginRequiredException();
    }

    return likeService.unlike(user.userId(), request);
  }
}
