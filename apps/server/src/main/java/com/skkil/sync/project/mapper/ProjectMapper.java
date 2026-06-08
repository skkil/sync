package com.skkil.sync.project.mapper;

import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import com.skkil.sync.project.model.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

  SearchProjectsResponse.Project toSearchProjectsResponseProject(Project project);

  GetProjectsResponse.Project toGetProjectsResponseProject(Project project);
}
