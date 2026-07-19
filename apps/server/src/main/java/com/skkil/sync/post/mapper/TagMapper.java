package com.skkil.sync.post.mapper;

import com.skkil.sync.post.dto.response.CreateTagResponse;
import com.skkil.sync.post.dto.summary.TagSummary;
import com.skkil.sync.post.model.Tag;
import java.util.Set;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {

  @Mapping(target = "projectHandle", source = "tag.project.handle")
  @Mapping(target = "followerCount", source = "tag.followerCount")
  @Mapping(target = "isFollowing", expression = "java(followedTagIds.contains(tag.getId()))")
  TagSummary toTagSummary(Tag tag, @Context Set<Long> followedTagIds);

  CreateTagResponse toCreateTagResponse(Tag tag);
}
