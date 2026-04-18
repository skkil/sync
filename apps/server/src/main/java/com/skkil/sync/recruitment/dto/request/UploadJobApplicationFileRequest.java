package com.skkil.sync.recruitment.dto.request;

import jakarta.validation.constraints.NotNull;

public record UploadJobApplicationFileRequest(@NotNull Long fileId) {}
