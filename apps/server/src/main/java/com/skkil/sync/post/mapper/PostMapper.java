package com.skkil.sync.post.mapper;

import com.skkil.sync.media.dto.MediaDto;
import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PostMapper {

  @Mappings({
    @Mapping(target = "author.name", source = "post.authorName"),
    @Mapping(target = "author.handle", source = "post.authorHandle"),
    @Mapping(target = "project.id", source = "post.projectId"),
    @Mapping(target = "project.name", source = "post.projectName"),
    @Mapping(target = "content", expression = "java(toContent(post, media))")
  })
  GetPostResponse toGetPostResponse(PostDto post, List<MediaDto> media);

  @Mappings({
    @Mapping(target = "json", source = "post.content"),
    @Mapping(target = "media", source = "media")
  })
  GetPostResponse.Content toContent(PostDto post, List<MediaDto> media);

  @Mappings({
    @Mapping(target = "author.name", source = "authorName"),
    @Mapping(target = "author.handle", source = "authorHandle"),
    @Mapping(target = "project.id", source = "projectId"),
    @Mapping(target = "project.name", source = "projectName")
  })
  GetPostsResponse.Post toPostResponse(PostDto postDto);
}
