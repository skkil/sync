package com.skkil.sync.post.mapper;

import com.skkil.sync.post.dto.response.SearchTagsResponse;
import com.skkil.sync.post.model.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {

  SearchTagsResponse.Tag toTag(Tag tag);
}
