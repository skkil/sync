package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.reflection.dto.response.GetTagsResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetTagsResponseSnippets {

  public static GetTagsResponse getGetTagsResponse() {
    return new GetTagsResponse(
        List.of(
            new GetTagsResponse.Tag(
                1L,
                "java",
                "자바 관련 태그",
                10L,
                true,
                LocalDateTime.of(2024, 1, 1, 0, 0),
                LocalDateTime.of(2024, 1, 1, 0, 0)),
            new GetTagsResponse.Tag(
                2L,
                "spring",
                "스프링 프레임워크 관련 태그",
                5L,
                false,
                LocalDateTime.of(2024, 1, 2, 0, 0),
                LocalDateTime.of(2024, 1, 2, 0, 0))));
  }

  public static ResponseFieldsSnippet getGetTagsResponseFields() {
    return responseFields(
        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("태그 목록"),
        fieldWithPath("tags[].id").type(JsonFieldType.NUMBER).description("태그 ID"),
        fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그 이름"),
        fieldWithPath("tags[].description").type(JsonFieldType.STRING).description("태그 설명"),
        fieldWithPath("tags[].postCount").type(JsonFieldType.NUMBER).description("태그가 사용된 게시물 수"),
        fieldWithPath("tags[].verified").type(JsonFieldType.BOOLEAN).description("태그 검증 여부"),
        fieldWithPath("tags[].createdAt").type(JsonFieldType.STRING).description("태그 생성일시"),
        fieldWithPath("tags[].updatedAt").type(JsonFieldType.STRING).description("태그 수정일시"));
  }
}
