package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.project.dto.response.GetProjectResponse;
import com.skkil.sync.project.model.Role;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectResponseSnippets {

  public static GetProjectResponse getGetProjectResponse() {
    return GetProjectResponse.builder()
        .summary(ProjectSummarySnippets.getProjectSummary())
        .teammates(
            List.of(
                ProjectTeammateSummarySnippets.getProjectTeammate(Role.ADMIN),
                ProjectTeammateSummarySnippets.getProjectTeammate(Role.MEMBER)))
        .hasMoreTeammates(false)
        .isViewer(true)
        .role(Role.ADMIN)
        .isOwner(true)
        .isFollowing(false)
        .hasPendingInvitation(false)
        .hasPendingJoinRequest(false)
        .build();
  }

  public static ResponseFieldsSnippet getGetProjectResponseFields() {
    return responseFields(
            fieldWithPath("summary").type(JsonFieldType.OBJECT).description("프로젝트 정보"))
        .and(
            ProjectSummarySnippets.getProjectSummaryFields("summary.")
                .toArray(FieldDescriptor[]::new))
        .and(
            ProjectTeammateSummarySnippets.getProjectTeammateFields("teammates[].")
                .toArray(FieldDescriptor[]::new))
        .and(
            fieldWithPath("teammates").type(JsonFieldType.ARRAY).description("팀원 목록"),
            fieldWithPath("hasMoreTeammates")
                .type(JsonFieldType.BOOLEAN)
                .description("추가 팀원 존재 여부"),
            fieldWithPath("isViewer").type(JsonFieldType.BOOLEAN).description("현재 사용자의 프로젝트 팀원 여부"),
            fieldWithPath("role")
                .type(RestDocsUtils.ENUM_TYPE)
                .optional()
                .description("현재 사용자 역할")
                .attributes(RestDocsUtils.getEnumAttributes(Role.class)),
            fieldWithPath("isOwner").type(JsonFieldType.BOOLEAN).description("현재 사용자의 프로젝트 소유자 여부"),
            fieldWithPath("isFollowing")
                .type(JsonFieldType.BOOLEAN)
                .description("현재 사용자의 프로젝트 팔로우 여부"),
            fieldWithPath("hasPendingInvitation")
                .type(JsonFieldType.BOOLEAN)
                .description("현재 사용자의 대기 중인 초대 존재 여부"),
            fieldWithPath("hasPendingJoinRequest")
                .type(JsonFieldType.BOOLEAN)
                .description("현재 사용자의 대기 중인 가입 요청 존재 여부"));
  }
}
