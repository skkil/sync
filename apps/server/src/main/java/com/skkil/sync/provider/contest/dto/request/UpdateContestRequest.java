package com.skkil.sync.provider.contest.dto.request;

import com.skkil.sync.provider.constant.ProviderType;
import com.skkil.sync.provider.dto.request.UpdateProviderRequest;

public record UpdateContestRequest(ProviderType type, String name, String description)
    implements UpdateProviderRequest {}
