package com.skkil.sync.user.controller;

import com.skkil.sync.user.constant.Handle;
import com.skkil.sync.user.dto.response.GetHandleAvailabilityResponse;
import com.skkil.sync.user.service.UserService;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
public class HandleController {

  private final UserService userService;

  public HandleController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("handles/availability")
  @ResponseStatus(HttpStatus.OK)
  public GetHandleAvailabilityResponse getHandleAvailability(
      @RequestParam @Size(min = Handle.MIN_LENGTH, max = Handle.MAX_LENGTH) String handle) {
    return userService.getHandleAvailability(handle);
  }
}
