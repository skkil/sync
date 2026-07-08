package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.GetProjectInvitationsResponse;
import com.skkil.sync.project.model.Role;
import com.skkil.sync.user.snippets.UserSummarySnippets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectInvitationsResponseSnippets {

  public static GetProjectInvitationsResponse getGetProjectInvitationsResponse() {
    return new GetProjectInvitationsResponse(
        List.of(
            new GetProjectInvitationsResponse.Invitation(
                ProjectInvitationSummarySnippets.getProjectInvitationSummary(
                    1L, Role.MEMBER, Instant.parse("2026-07-09T00:00:00Z")),
                UserSummarySnippets.getUserSummary())));
  }

  public static ResponseFieldsSnippet getGetProjectInvitationsResponseFields() {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath("invitations").type(JsonFieldType.ARRAY).description("초대 목록"));
    fields.add(
        fieldWithPath("invitations[].invitation").type(JsonFieldType.OBJECT).description("초대 정보"));
    fields.addAll(
        ProjectInvitationSummarySnippets.getProjectInvitationSummaryFields(
            "invitations[].invitation."));
    fields.add(
        fieldWithPath("invitations[].invitee")
            .type(JsonFieldType.OBJECT)
            .description("초대받은 유저 정보"));
    fields.addAll(UserSummarySnippets.getUserSummaryFields("invitations[].invitee."));

    return responseFields(fields.toArray(FieldDescriptor[]::new));
  }
}
