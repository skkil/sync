package com.skkil.sync.project.dto.response;

import com.skkil.sync.project.dto.summary.MyProjectSummary;
import java.util.List;

public record GetMyProjectsResponse(List<MyProjectSummary> projects) {}
