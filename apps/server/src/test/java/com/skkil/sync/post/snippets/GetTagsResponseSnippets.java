package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.GetTagsResponse;
import com.skkil.sync.post.dto.summary.TagSummary;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetTagsResponseSnippets {

  public static GetTagsResponse getGetTagsResponse() {
    return new GetTagsResponse(
        List.of(
            TagSummary.builder()
                .id(1L)
                .name("java")
                .description("자바 관련 태그")
                .postCount(10L)
                .followerCount(3L)
                .isFollowing(false)
                .build(),
            TagSummary.builder()
                .id(2L)
                .name("spring")
                .description("스프링 프레임워크 관련 태그")
                .postCount(5L)
                .followerCount(1L)
                .isFollowing(true)
                .build()));
  }

  public static ResponseFieldsSnippet getGetTagsResponseFields() {
    return responseFields(
        fieldWithPath("tags").type(JsonFieldType.ARRAY).description("태그 목록"),
        fieldWithPath("tags[].id").type(JsonFieldType.NUMBER).description("태그 ID"),
        fieldWithPath("tags[].name").type(JsonFieldType.STRING).description("태그 이름"),
        fieldWithPath("tags[].description").type(JsonFieldType.STRING).description("태그 설명"),
        fieldWithPath("tags[].postCount").type(JsonFieldType.NUMBER).description("태그가 사용된 게시물 수"),
        fieldWithPath("tags[].followerCount")
            .type(JsonFieldType.NUMBER)
            .description("태그를 팔로우하는 사용자 수"),
        fieldWithPath("tags[].projectHandle")
            .type(JsonFieldType.STRING)
            .description("프로젝트 태그인 경우 해당 프로젝트의 핸들 (전역 태그인 경우 없음)")
            .optional(),
        fieldWithPath("tags[].isFollowing")
            .type(JsonFieldType.BOOLEAN)
            .description("요청자가 해당 태그를 팔로우하고 있는지 여부 (프로젝트 태그는 항상 false)"));
  }
}
