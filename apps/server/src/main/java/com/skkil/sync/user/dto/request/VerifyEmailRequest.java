package com.skkil.sync.user.dto.request;

import com.skkil.sync.user.constant.EmailVerificationConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VerifyEmailRequest(
    @NotNull
        @Size(
            min = EmailVerificationConstants.EMAIL_VERIFICATION_TOKEN_LENGTH,
            max = EmailVerificationConstants.EMAIL_VERIFICATION_TOKEN_LENGTH)
        String token) {}
