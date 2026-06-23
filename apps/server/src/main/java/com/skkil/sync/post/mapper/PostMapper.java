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
    @Mapping(target = "author", expression = "java(toGetPostAuthor(postDto))"),
    @Mapping(target = "project", expression = "java(toGetPostProject(postDto))")
  })
  GetPostResponse toGetPostResponse(PostDto postDto);

  @Mappings({
    @Mapping(target = "author", expression = "java(toPostAuthor(postDto))"),
    @Mapping(target = "project", expression = "java(toPostProject(postDto))")
  })
  GetPostsResponse.Post toPostResponse(PostDto postDto);

  default GetPostResponse.Author toGetPostAuthor(PostDto postDto) {
    return new GetPostResponse.Author(postDto.authorId(), postDto.authorName());
  }

  default GetPostResponse.Project toGetPostProject(PostDto postDto) {
    if (postDto.projectId() == null) {
      return null;
    }

    return new GetPostResponse.Project(postDto.projectId(), postDto.projectName());
  }

  default GetPostsResponse.Author toPostAuthor(PostDto postDto) {
    return new GetPostsResponse.Author(postDto.authorId(), postDto.authorName());
  }

  default GetPostsResponse.Project toPostProject(PostDto postDto) {
    if (postDto.projectId() == null) {
      return null;
    }

    return new GetPostsResponse.Project(postDto.projectId(), postDto.projectName());
  }
}
