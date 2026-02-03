package com.skkil.sync.lab.controller;

import com.skkil.sync.lab.dto.request.AddMemberReviewRequest;
import com.skkil.sync.lab.dto.request.CreateLabRequest;
import com.skkil.sync.lab.dto.request.UpdateLabRequest;
import com.skkil.sync.lab.dto.response.CreateLabResponse;
import com.skkil.sync.lab.dto.response.GetLabDetailResponse;
import com.skkil.sync.lab.dto.response.GetLabResponse;
import com.skkil.sync.lab.dto.response.LabMemberResponse;
import com.skkil.sync.lab.dto.response.LabSearchResponse;
import com.skkil.sync.lab.service.LabService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public CreateLabResponse createLab(@RequestBody @Validated CreateLabRequest request) {
    return labService.createLab(request);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public GetLabResponse getLab(@PathVariable Long id) {
    return labService.getLab(id);
  }

  @GetMapping("/{id}/detail")
  @ResponseStatus(HttpStatus.OK)
  public GetLabDetailResponse getLabDetail(@PathVariable Long id) {
    return labService.getLabDetail(id);
  }

  @PatchMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void updateLab(@PathVariable Long id, @RequestBody @Validated UpdateLabRequest request) {
    labService.updateLab(id, request);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void deleteLab(@PathVariable Long id) {
    labService.deleteLab(id);
  }

  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public Page<LabSearchResponse> searchLabs(
      @RequestParam String keyword,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return labService.searchLabs(keyword, pageable);
  }

  @GetMapping("/professor/{professorId}")
  @ResponseStatus(HttpStatus.OK)
  public Page<LabSearchResponse> getLabsByProfessor(
      @PathVariable Long professorId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    Pageable pageable = PageRequest.of(page, size);
    return labService.getLabsByProfessor(professorId, pageable);
  }

  @PostMapping("/{labId}/members/{userId}")
  @ResponseStatus(HttpStatus.CREATED)
  public LabMemberResponse addMemberWithReview(
      @PathVariable Long labId,
      @PathVariable Long userId,
      @RequestBody @Validated AddMemberReviewRequest request) {
    return labService.addMemberWithReview(labId, userId, request);
  }

  @PatchMapping("/{labId}/members/{userId}/review")
  @ResponseStatus(HttpStatus.OK)
  public void updateMemberReview(
      @PathVariable Long labId,
      @PathVariable Long userId,
      @RequestBody @Validated AddMemberReviewRequest request) {
    labService.updateMemberReview(labId, userId, request);
  }

  @DeleteMapping("/{labId}/members/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void removeMember(@PathVariable Long labId, @PathVariable Long userId) {
    labService.removeMember(labId, userId);
  }
}
