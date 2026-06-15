package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.GetProjectResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectResponseSnippets {

  public static GetProjectResponse getGetProjectResponse() {
    return GetProjectResponse.builder()
        .handle("my-project")
        .name("나의 프로젝트")
        .description("프로젝트 설명")
        .teammates(
            List.of(
                new GetProjectResponse.Teammate("1", true),
                new GetProjectResponse.Teammate("2", false)))
        .hasMoreTeammates(false)
        .isAdmin(true)
        .isMember(true)
        .recentActivities(
            List.of(new GetProjectResponse.Activity("1", "2024-01-01T00:00:00Z", "활동 내용")))
        .build();
  }

  public static ResponseFieldsSnippet getGetProjectResponseFields() {
    return responseFields(
        fieldWithPath("handle").type(JsonFieldType.STRING).description("프로젝트 핸들"),
        fieldWithPath("name").type(JsonFieldType.STRING).description("프로젝트 이름"),
        fieldWithPath("description").type(JsonFieldType.STRING).optional().description("프로젝트 설명"),
        fieldWithPath("isPublic").type(JsonFieldType.BOOLEAN).description("공개 여부"),
        fieldWithPath("teammates").type(JsonFieldType.ARRAY).description("팀원 목록"),
        fieldWithPath("teammates[].id").type(JsonFieldType.STRING).description("팀원 ID"),
        fieldWithPath("teammates[].isOwner").type(JsonFieldType.BOOLEAN).description("오너 여부"),
        fieldWithPath("hasMoreTeammates").type(JsonFieldType.BOOLEAN).description("추가 팀원 존재 여부"),
        fieldWithPath("isAdmin").type(JsonFieldType.BOOLEAN).description("관리자 여부"),
        fieldWithPath("isMember").type(JsonFieldType.BOOLEAN).description("멤버 여부"),
        fieldWithPath("recentActivities")
            .type(JsonFieldType.ARRAY)
            .description("최근 활동 목록")
            .optional(),
        fieldWithPath("recentActivities[].id").type(JsonFieldType.STRING).description("활동 ID"),
        fieldWithPath("recentActivities[].timestamp")
            .type(JsonFieldType.STRING)
            .description("활동 시각"),
        fieldWithPath("recentActivities[].text").type(JsonFieldType.STRING).description("활동 내용"));
  }
}
