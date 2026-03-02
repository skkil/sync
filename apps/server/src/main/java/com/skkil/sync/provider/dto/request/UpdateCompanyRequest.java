package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;

public record UpdateCompanyRequest(
    ProviderType type, String name, String description, String contactInfo, String industry)
    implements UpdateProviderRequest {}
