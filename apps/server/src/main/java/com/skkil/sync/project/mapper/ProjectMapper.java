package com.skkil.sync.project.mapper;

import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.dto.response.GetProjectsResponse;
import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import com.skkil.sync.project.model.Project;
import com.skkil.sync.project.model.Teammate;
import java.net.URL;
import java.util.Map;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

  SearchProjectsResponse.Project toSearchProjectsResponseProject(Project project);

  GetProjectsResponse.Project toGetProjectsResponseProject(Project project);

  default GetProjectResponse.Teammate toGetProjectResponseTeammate(
      Teammate teammate, Map<Long, URL> profileImageUrls) {
    var profileImage = teammate.getUser().getProfileImage();
    var url = profileImage != null ? profileImageUrls.get(profileImage.getId()) : null;
    return GetProjectResponse.Teammate.builder()
        .handle(teammate.getUser().getHandle())
        .name(teammate.getUser().getFullName())
        .role(teammate.getRole())
        .profileImageUrl(url != null ? url.toString() : null)
        .build();
  }

  default GetProjectTeammatesResponse.Teammate toGetProjectTeammatesResponseTeammate(
      Teammate teammate, Map<Long, URL> profileImageUrls) {
    var profileImage = teammate.getUser().getProfileImage();
    var url = profileImage != null ? profileImageUrls.get(profileImage.getId()) : null;
    return GetProjectTeammatesResponse.Teammate.builder()
        .handle(teammate.getUser().getHandle())
        .name(teammate.getUser().getFullName())
        .role(teammate.getRole())
        .profileImageUrl(url != null ? url.toString() : null)
        .build();
  }
}
