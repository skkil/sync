package com.skkil.sync.user.dto.response;

import com.skkil.sync.user.dto.summary.UserSummary;
import java.util.List;

public record SearchUsersResponse(List<UserSummary> users) {}
