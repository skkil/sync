package com.skkil.sync.post.mapper;

import com.skkil.sync.post.dto.data.SummaryDto;
import com.skkil.sync.post.dto.response.GetSummariesResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PostSummaryMapper {

  GetSummariesResponse.Summary toSummaryResponse(SummaryDto summaryDto);
}
