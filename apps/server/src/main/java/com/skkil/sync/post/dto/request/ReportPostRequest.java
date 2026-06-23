package com.skkil.sync.post.dto.request;

import com.skkil.sync.post.model.PostReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReportPostRequest(
    @NotNull PostReportReason reason, @Size(max = 1000) String description) {}
