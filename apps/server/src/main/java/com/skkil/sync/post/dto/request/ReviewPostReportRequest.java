package com.skkil.sync.post.dto.request;

import com.skkil.sync.post.model.PostReportResolution;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewPostReportRequest(
    @NotNull PostReportResolution resolution,
    @Size(max = 1000) String resolutionNote,
    @Size(max = 1000) String hiddenReason) {}
