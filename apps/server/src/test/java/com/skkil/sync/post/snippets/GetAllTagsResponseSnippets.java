package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.OffsetPaginationResponseSnippets;
import com.skkil.sync.post.dto.response.GetAllTagsResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetAllTagsResponseSnippets {

  public static GetAllTagsResponse getGetAllTagsResponse() {
    return new GetAllTagsResponse(
        OffsetPaginationResponseSnippets.of(GetTagsResponseSnippets.getGetTagsResponse().tags()));
  }

  public static ResponseFieldsSnippet getGetAllTagsResponseFields() {
    FieldDescriptors fields = OffsetPaginationResponseSnippets.getPaginationResponseFields("tags");

    fields =
        fields.andWithPrefix(
            "tags.content[]",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("태그 ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("태그 이름"),
            fieldWithPath(".description").type(JsonFieldType.STRING).description("태그 설명"),
            fieldWithPath(".postCount").type(JsonFieldType.NUMBER).description("태그가 사용된 게시물 수"),
            fieldWithPath(".followerCount")
                .type(JsonFieldType.NUMBER)
                .description("태그를 팔로우하는 사용자 수"),
            fieldWithPath(".projectHandle")
                .type(JsonFieldType.STRING)
                .description("프로젝트 태그인 경우 해당 프로젝트의 핸들 (전역 태그인 경우 없음)")
                .optional(),
            fieldWithPath(".isFollowing")
                .type(JsonFieldType.BOOLEAN)
                .description("요청자가 해당 태그를 팔로우하고 있는지 여부 (프로젝트 태그는 항상 false)"));

    return responseFields(fields.getFieldDescriptors());
  }
}
