package com.skkil.sync.auth.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CsrfController {

  @GetMapping("/csrf")
  public CsrfToken getCsrfToken(CsrfToken csrfToken) {
    return csrfToken;
  }
}
