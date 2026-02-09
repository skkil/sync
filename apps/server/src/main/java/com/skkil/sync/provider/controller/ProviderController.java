package com.skkil.sync.provider.controller;

import com.skkil.sync.provider.dto.request.CreateProviderRequest;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;
import com.skkil.sync.provider.dto.response.CreateProviderResponse;
import com.skkil.sync.provider.dto.response.GetProviderResponse;
import com.skkil.sync.provider.service.ProviderService;
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
public class ProviderController {

  private final ProviderService providerService;

  public ProviderController(ProviderService providerService) {
    this.providerService = providerService;
  }

  @PostMapping("/providers")
  @ResponseStatus(HttpStatus.CREATED)
  public CreateProviderResponse createProvider(
      @RequestBody @Validated CreateProviderRequest request) {
    return providerService.createProvider(request);
  }

  @GetMapping("/providers/{id}")
  @ResponseStatus(HttpStatus.OK)
  public GetProviderResponse getProvider(@PathVariable Long id) {
    return providerService.getProvider(id);
  }

  @PatchMapping("/providers/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateProvider(
      @PathVariable Long id, @RequestBody @Validated UpdateProviderRequest request) {
    providerService.updateProvider(id, request);
  }

  @DeleteMapping("/providers/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteProvider(@PathVariable Long id) {
    providerService.deleteProvider(id);
  }

}
