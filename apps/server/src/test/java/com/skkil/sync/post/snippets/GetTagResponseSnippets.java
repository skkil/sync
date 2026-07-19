package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.GetTagResponse;
import com.skkil.sync.post.dto.summary.TagSummary;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetTagResponseSnippets {

  public static GetTagResponse getGetTagResponse() {
    return new GetTagResponse(
        TagSummary.builder()
            .id(1L)
            .name("java")
            .description("자바 관련 태그")
            .postCount(10L)
            .followerCount(3L)
            .isFollowing(false)
            .build());
  }

  public static ResponseFieldsSnippet getGetTagResponseFields() {
    return responseFields(
        fieldWithPath("tag.id").type(JsonFieldType.NUMBER).description("태그 ID"),
        fieldWithPath("tag.name").type(JsonFieldType.STRING).description("태그 이름"),
        fieldWithPath("tag.description").type(JsonFieldType.STRING).description("태그 설명"),
        fieldWithPath("tag.postCount").type(JsonFieldType.NUMBER).description("태그가 사용된 게시물 수"),
        fieldWithPath("tag.followerCount")
            .type(JsonFieldType.NUMBER)
            .description("태그를 팔로우하는 사용자 수"),
        fieldWithPath("tag.projectHandle")
            .type(JsonFieldType.STRING)
            .description("프로젝트 태그인 경우 해당 프로젝트의 핸들 (전역 태그인 경우 없음)")
            .optional(),
        fieldWithPath("tag.isFollowing")
            .type(JsonFieldType.BOOLEAN)
            .description("요청자가 해당 태그를 팔로우하고 있는지 여부 (프로젝트 태그는 항상 false)"));
  }
}
