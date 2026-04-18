package com.skkil.sync.provider.mapper;

import com.skkil.sync.provider.dto.data.ProviderDto;
import com.skkil.sync.provider.dto.response.GetProvidersResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ProviderMapper {

  @Mappings({
    @Mapping(target = "isVerified", expression = "java(provider.verifiedByUserId() != null)"),
  })
  GetProvidersResponse.Provider toProviderDto(ProviderDto provider);
}
