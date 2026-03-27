package com.skkil.sync.reflection.mapper;

import com.skkil.sync.reflection.dto.data.ReflectionDto;
import com.skkil.sync.reflection.dto.response.GetReflectionsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ReflectionMapper {

  @Mappings({
    @Mapping(target = "author.id", source = "authorId"),
    @Mapping(target = "author.name", source = "authorName"),
    @Mapping(target = "project.id", source = "projectId"),
    @Mapping(target = "project.name", source = "projectName"),
  })
  GetReflectionsResponse.Reflection toReflectionResponse(ReflectionDto reflectionDto);
}
