package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.GetMyProjectInvitationsResponse;
import com.skkil.sync.project.model.Role;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetMyProjectInvitationsResponseSnippets {

  public static GetMyProjectInvitationsResponse getGetMyProjectInvitationsResponse() {
    return new GetMyProjectInvitationsResponse(
        List.of(
            new GetMyProjectInvitationsResponse.Invitation(
                ProjectInvitationSummarySnippets.getProjectInvitationSummary(
                    1L, Role.MEMBER, Instant.parse("2026-07-09T00:00:00Z")),
                "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                ProjectSummarySnippets.getProjectSummary())));
  }

  public static ResponseFieldsSnippet getGetMyProjectInvitationsResponseFields() {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath("invitations").type(JsonFieldType.ARRAY).description("초대 목록"));
    fields.add(
        fieldWithPath("invitations[].invitation").type(JsonFieldType.OBJECT).description("초대 정보"));
    fields.addAll(
        ProjectInvitationSummarySnippets.getProjectInvitationSummaryFields(
            "invitations[].invitation."));
    fields.add(
        fieldWithPath("invitations[].token").type(JsonFieldType.STRING).description("초대 토큰"));
    fields.add(
        fieldWithPath("invitations[].project").type(JsonFieldType.OBJECT).description("프로젝트 정보"));
    fields.addAll(ProjectSummarySnippets.getProjectSummaryFields("invitations[].project."));

    return responseFields(fields.toArray(FieldDescriptor[]::new));
  }
}
