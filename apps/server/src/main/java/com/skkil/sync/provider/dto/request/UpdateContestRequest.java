package com.skkil.sync.provider.dto.request;

import com.skkil.sync.provider.constant.ProviderType;

public record UpdateContestRequest(ProviderType type, String name, String description)
    implements UpdateProviderRequest {}
