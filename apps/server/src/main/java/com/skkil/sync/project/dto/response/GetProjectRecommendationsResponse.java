package com.skkil.sync.project.dto.response;

import com.skkil.sync.project.dto.summary.ProjectSummary;
import java.util.List;

public record GetProjectRecommendationsResponse(List<ProjectSummary> projects) {}
