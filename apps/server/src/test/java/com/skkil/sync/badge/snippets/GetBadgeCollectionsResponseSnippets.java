package com.skkil.sync.badge.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.badge.dto.response.GetBadgeCollectionsResponse;
import java.time.Instant;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetBadgeCollectionsResponseSnippets {

  public static GetBadgeCollectionsResponse getGetBadgeCollectionsResponse() {
    return new GetBadgeCollectionsResponse(
        List.of(
            GetBadgeCollectionsResponse.Badge.builder()
                .id(1L)
                .tag(
                    GetBadgeCollectionsResponse.Tag.builder()
                        .id(1L)
                        .name("java")
                        .description("자바 관련 태그")
                        .imageUrl("https://example.com/badges/java.png")
                        .build())
                .postCount(10L)
                .level(3)
                .createdAt(Instant.parse("2026-05-27T10:00:00Z"))
                .build()));
  }

  public static ResponseFieldsSnippet getBadgeCollectionsResponseFields() {
    return responseFields(
        fieldWithPath("badges").type(JsonFieldType.ARRAY).description("뱃지 도감 목록"),
        fieldWithPath("badges[].id").type(JsonFieldType.NUMBER).description("도감 항목 ID"),
        fieldWithPath("badges[].tag").type(JsonFieldType.OBJECT).description("뱃지 태그"),
        fieldWithPath("badges[].tag.id").type(JsonFieldType.NUMBER).description("태그 ID"),
        fieldWithPath("badges[].tag.name").type(JsonFieldType.STRING).description("태그 이름"),
        fieldWithPath("badges[].tag.description").type(JsonFieldType.STRING).description("태그 설명"),
        fieldWithPath("badges[].tag.imageUrl")
            .type(JsonFieldType.STRING)
            .description("뱃지 이미지 URL")
            .optional(),
        fieldWithPath("badges[].postCount")
            .type(JsonFieldType.NUMBER)
            .description("해당 태그로 작성한 글 수"),
        fieldWithPath("badges[].level").type(JsonFieldType.NUMBER).description("뱃지 레벨"),
        fieldWithPath("badges[].createdAt").type(JsonFieldType.STRING).description("뱃지 최초 획득 시각"));
  }
}
