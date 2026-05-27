package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.reflection.dto.response.SearchTagsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class SearchTagsResponseSnippets {

  public static SearchTagsResponse getSearchTagsResponse() {
    return new SearchTagsResponse(
        List.of(
            SearchTagsResponse.Tag.builder()
                .name("java")
                .description("자바 관련 태그")
                .postCount(10L)
                .build(),
            SearchTagsResponse.Tag.builder()
                .name("spring")
                .description("스프링 프레임워크 관련 태그")
                .postCount(5L)
                .build()));
  }

  public static ResponseFieldsSnippet getSearchTagsResponseFields() {
    return responseFields(
        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("태그 목록"),
        fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그 이름"),
        fieldWithPath("tags[].description").type(JsonFieldType.STRING).description("태그 설명"),
        fieldWithPath("tags[].postCount").type(JsonFieldType.NUMBER).description("태그가 사용된 게시물 수"));
  }
}
