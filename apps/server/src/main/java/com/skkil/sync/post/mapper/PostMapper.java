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
    @Mapping(target = "author", expression = "java(toGetPostAuthor(post))"),
    @Mapping(target = "project", expression = "java(toGetPostProject(post))"),
    @Mapping(target = "content", expression = "java(toContent(post, media))")
  })
  GetPostResponse toGetPostResponse(PostDto post, List<MediaDto> media);

  @Mappings({
    @Mapping(target = "json", source = "post.content"),
    @Mapping(target = "media", source = "media")
  })
  GetPostResponse.Content toContent(PostDto post, List<MediaDto> media);

  @Mappings({
    @Mapping(target = "author", expression = "java(toPostAuthor(postDto))"),
    @Mapping(target = "project", expression = "java(toPostProject(postDto))")
  })
  GetPostsResponse.Post toPostResponse(PostDto postDto);

  default GetPostResponse.Author toGetPostAuthor(PostDto postDto) {
    return new GetPostResponse.Author(postDto.authorName(), postDto.authorHandle());
  }

  default GetPostResponse.Project toGetPostProject(PostDto postDto) {
    if (postDto.projectHandle() == null) {
      return null;
    }

    return new GetPostResponse.Project(postDto.projectHandle(), postDto.projectName());
  }

  default GetPostsResponse.Author toPostAuthor(PostDto postDto) {
    return new GetPostsResponse.Author(postDto.authorName(), postDto.authorHandle());
  }

  default GetPostsResponse.Project toPostProject(PostDto postDto) {
    if (postDto.projectHandle() == null) {
      return null;
    }

    return new GetPostsResponse.Project(postDto.projectHandle(), postDto.projectName());
  }
}
