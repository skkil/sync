package com.skkil.sync.media.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record UploadMediaRequest(
    @NotBlank String mediaType, @NotBlank String fileName, @NotNull @Positive Long fileSize) {}
