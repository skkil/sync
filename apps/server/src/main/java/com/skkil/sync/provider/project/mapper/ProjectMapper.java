package com.skkil.sync.provider.project.mapper;

import com.skkil.sync.provider.project.dto.data.TeamBuildingPostDto;
import com.skkil.sync.provider.project.dto.response.GetTeamBuildingPostsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

  @Mappings({
    @Mapping(target = "project.id", source = "projectId"),
    @Mapping(target = "project.name", source = "projectName"),
    @Mapping(target = "project.description", source = "projectDescription")
  })
  GetTeamBuildingPostsResponse.Post toPostResponse(TeamBuildingPostDto post);
}
