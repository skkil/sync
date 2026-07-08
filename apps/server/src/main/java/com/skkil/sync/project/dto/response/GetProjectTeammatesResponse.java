package com.skkil.sync.project.dto.response;

import com.skkil.sync.project.dto.summary.ProjectTeammateSummary;
import java.util.List;

public record GetProjectTeammatesResponse(List<ProjectTeammateSummary> teammates) {}
