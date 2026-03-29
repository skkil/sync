package com.skkil.sync.provider.project.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;

public record UpdateProjectRequest(
    ProviderType type, String name, String description, String contactInfo)
    implements UpdateProviderRequest {}
