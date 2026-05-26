package com.skkil.sync.reflection.mapper;

import com.skkil.sync.reflection.dto.data.SummaryDto;
import com.skkil.sync.reflection.dto.response.GetSummariesResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReflectionSummaryMapper {

  GetSummariesResponse.Summary toSummaryResponse(SummaryDto summaryDto);
}
