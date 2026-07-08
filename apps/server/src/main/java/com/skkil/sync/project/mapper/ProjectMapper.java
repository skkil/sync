package com.skkil.sync.project.mapper;

import com.skkil.sync.project.dto.summary.ProjectSummary;
import com.skkil.sync.project.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

  @Mapping(target = "isPublic", source = "project.public")
  ProjectSummary toProjectSummary(Project project, String iconUrl);
}
