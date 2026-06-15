package com.skkil.sync.project.mapper;

import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

  SearchProjectsResponse.Project toSearchProjectsResponseProject(Project project);

  GetProjectsResponse.Project toGetProjectsResponseProject(Project project);

  @Mappings({
    @Mapping(target = "handle", source = "teammate.user.handle"),
    @Mapping(target = "name", source = "teammate.user.fullName"),
  })
  GetProjectResponse.Teammate toGetProjectResponseTeammate(Teammate teammate);

  @Mappings({
    @Mapping(target = "handle", source = "teammate.user.handle"),
    @Mapping(target = "name", source = "teammate.user.fullName"),
  })
  GetProjectTeammatesResponse.Teammate toGetProjectTeammatesResponseTeammate(Teammate teammate);
}
