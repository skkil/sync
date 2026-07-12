package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.GetTagsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetTagsResponseSnippets {

  public static GetTagsResponse getGetTagsResponse() {
    return new GetTagsResponse(
        List.of(
            GetTagsResponse.Tag.builder()
                .id(1L)
                .name("java")
                .description("자바 관련 태그")
                .postCount(10L)
                .build(),
            GetTagsResponse.Tag.builder()
                .id(2L)
                .name("spring")
                .description("스프링 프레임워크 관련 태그")
                .postCount(5L)
                .build()));
  }

  public static ResponseFieldsSnippet getGetTagsResponseFields() {
    return responseFields(
        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("태그 목록"),
        fieldWithPath("tags[].id").type(JsonFieldType.NUMBER).description("태그 ID"),
        fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그 이름"),
        fieldWithPath("tags[].description").type(JsonFieldType.STRING).description("태그 설명"),
        fieldWithPath("tags[].postCount").type(JsonFieldType.NUMBER).description("태그가 사용된 게시물 수"));
  }
}
