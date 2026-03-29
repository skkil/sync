package com.skkil.sync.provider.company.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;

public record UpdateCompanyRequest(
    ProviderType type, String name, String description, String contactInfo, String industry)
    implements UpdateProviderRequest {}
