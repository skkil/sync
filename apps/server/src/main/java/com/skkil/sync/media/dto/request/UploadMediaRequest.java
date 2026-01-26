package com.skkil.sync.media.dto.request;

import com.skkil.sync.media.constant.MediaContext;
import jakarta.validation.constraints.NotNull;

public record UploadMediaRequest(
    @NotNull String mediaType,
    @NotNull MediaContext mediaContext,
    @NotNull String fileName,
    @NotNull Long fileSize) {}
