package com.skkil.sync.post.mapper;

import com.skkil.sync.post.dto.response.GetPostActivitiesResponse;
import com.skkil.sync.post.model.PostActivity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostActivityMapper {

  GetPostActivitiesResponse.Activity toActivity(PostActivity activity);
}
