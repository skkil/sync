package com.skkil.sync.common.integration.email.dto;

import lombok.Builder;

@Builder
public record EmailMessage(String to, String subject, String text) {}
