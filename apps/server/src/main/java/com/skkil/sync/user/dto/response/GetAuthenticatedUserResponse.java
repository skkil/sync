package com.skkil.sync.user.dto.response;

public record GetAuthenticatedUserResponse(Long userId, String fullName, String email) {}
