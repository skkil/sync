package com.skkil.sync.post.mapper;

import com.skkil.sync.post.dto.data.PostDto;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface PostMapper {

  @Mappings({
    @Mapping(target = "author.id", source = "authorId"),
    @Mapping(target = "author.name", source = "authorName"),
    @Mapping(target = "project.id", source = "projectId"),
    @Mapping(target = "project.name", source = "projectName")
  })
  GetPostResponse toGetPostResponse(PostDto postDto);

  @Mappings({
    @Mapping(target = "author.id", source = "authorId"),
    @Mapping(target = "author.name", source = "authorName"),
    @Mapping(target = "project.id", source = "projectId"),
    @Mapping(target = "project.name", source = "projectName")
  })
  GetPostsResponse.Post toPostResponse(PostDto postDto);
}
