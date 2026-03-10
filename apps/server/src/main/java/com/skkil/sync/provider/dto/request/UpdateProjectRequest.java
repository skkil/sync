package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;

public record UpdateProjectRequest(
    ProviderType type, String name, String description, String contactInfo)
    implements UpdateProviderRequest {}
