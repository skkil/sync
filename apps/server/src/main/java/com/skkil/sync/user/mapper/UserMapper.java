package com.skkil.sync.user.mapper;

import com.skkil.sync.user.dto.summary.UserSummary;
import com.skkil.sync.user.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "name", source = "user.fullName")
  UserSummary toUserSummary(User user, String profileImageUrl);
}
