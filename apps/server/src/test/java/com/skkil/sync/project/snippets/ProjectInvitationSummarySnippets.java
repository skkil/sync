package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.project.dto.summary.ProjectInvitationSummary;
import com.skkil.sync.project.model.Role;
import com.skkil.sync.user.snippets.UserSummarySnippets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class ProjectInvitationSummarySnippets {

  public static ProjectInvitationSummary getProjectInvitationSummary(
      Long id, Role role, Instant expiresAt) {
    return ProjectInvitationSummary.builder()
        .id(id)
        .inviter(UserSummarySnippets.getUserSummary())
        .role(role)
        .expiresAt(expiresAt)
        .build();
  }

  public static List<FieldDescriptor> getProjectInvitationSummaryFields(String prefix) {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath(prefix + "id").type(JsonFieldType.NUMBER).description("초대 ID"));
    fields.add(
        fieldWithPath(prefix + "inviter").type(JsonFieldType.OBJECT).description("초대한 유저 정보"));
    fields.addAll(UserSummarySnippets.getUserSummaryFields(prefix + "inviter."));
    fields.add(
        fieldWithPath(prefix + "role")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("초대받은 유저의 역할")
            .attributes(RestDocsUtils.getEnumAttributes(Role.class)));
    fields.add(
        fieldWithPath(prefix + "expiresAt").type(JsonFieldType.STRING).description("초대 만료 시각"));
    return fields;
  }
}
