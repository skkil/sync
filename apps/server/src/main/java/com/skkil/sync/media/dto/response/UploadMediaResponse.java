package com.skkil.sync.media.dto.response;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record UploadMediaResponse(Long mediaId, String uploadUrl, LocalDateTime expiresAt) {}
