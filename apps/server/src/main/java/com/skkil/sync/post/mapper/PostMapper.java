package com.skkil.sync.post.mapper;

import com.skkil.sync.media.dto.MediaDto;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.summary.PostSummary;
import com.skkil.sync.post.dto.summary.TagSummary;
import com.skkil.sync.project.dto.summary.ProjectSummary;
import com.skkil.sync.user.dto.summary.UserSummary;
import java.util.List;
import org.jspecify.annotations.Nullable;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PostMapper {

  PostSummary toPostSummary(
      PostDto post,
      UserSummary author,
      @Nullable ProjectSummary project,
      boolean isAuthor,
      List<TagSummary> tags,
      List<GetPostResponse.Media> previewMedia);

  List<GetPostResponse.Media> toPreviewMedia(List<MediaDto> media);

  @Mappings({
    @Mapping(target = "json", source = "post.content"),
    @Mapping(target = "media", source = "media")
  })
  GetPostResponse.Content toContent(PostDto post, List<MediaDto> media);

  @Mapping(target = "handle", source = "projectHandle")
  @Mapping(target = "name", source = "projectName")
  @Mapping(target = "description", source = "projectDescription")
  @Mapping(target = "website", source = "projectWebsite")
  @Mapping(target = "isPublic", source = "projectIsPublic")
  @Mapping(target = "iconUrl", ignore = true)
  ProjectSummary toProjectSummary(PostDto post);
}
