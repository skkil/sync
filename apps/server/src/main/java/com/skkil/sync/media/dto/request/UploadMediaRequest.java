package com.skkil.sync.media.dto.request;

import com.skkil.sync.media.constant.MediaContext;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UploadMediaRequest(
    @NotBlank String mediaType,
    @NotNull MediaContext mediaContext,
    @NotBlank String fileName,
    @NotNull @Positive Long fileSize) {}
