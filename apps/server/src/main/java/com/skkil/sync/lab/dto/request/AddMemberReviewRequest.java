package com.skkil.sync.lab.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AddMemberReviewRequest(@NotBlank(message = "Review cannot be blank") String review) {}
