package com.skkil.sync.user.mapper;

import com.skkil.sync.user.dto.request.UpdateProfileRequest;
import com.skkil.sync.user.dto.response.GetProfileResponse;
import com.skkil.sync.user.model.UserContacts;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

  UserContacts toUserContacts(UpdateProfileRequest.Contacts contacts);

  GetProfileResponse.Contacts toGetProfileResponseContacts(UserContacts contacts);
}
