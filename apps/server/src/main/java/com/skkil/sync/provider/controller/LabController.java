package com.skkil.sync.provider.controller;

import com.skkil.sync.provider.model.Lab;
import com.skkil.sync.provider.service.LabService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/labs")
public class LabController {

  private final LabService labService;

  public LabController(LabService labService) {
    this.labService = labService;
  }

  @GetMapping("/professor/{professorId}")
  @ResponseStatus(HttpStatus.OK)
  public Page<Lab> getLabsByProfessor(
      @PathVariable String professorId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return labService.getLabsByProfessor(professorId, pageable);
  }
}
