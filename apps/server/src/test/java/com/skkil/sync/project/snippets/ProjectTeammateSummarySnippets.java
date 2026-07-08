package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.project.dto.summary.ProjectTeammateSummary;
import com.skkil.sync.project.model.Role;
import com.skkil.sync.user.snippets.UserSummarySnippets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

public class ProjectTeammateSummarySnippets {

  public static ProjectTeammateSummary getProjectTeammate(Role role) {
    return ProjectTeammateSummary.builder()
        .user(UserSummarySnippets.getUserSummary())
        .role(role)
        .build();
  }

  public static List<FieldDescriptor> getProjectTeammateFields(String prefix) {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath(prefix + "user").type(JsonFieldType.OBJECT).description("팀원 유저 정보"));
    fields.addAll(UserSummarySnippets.getUserSummaryFields(prefix + "user."));
    fields.add(
        fieldWithPath(prefix + "role")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("팀원 역할")
            .attributes(RestDocsUtils.getEnumAttributes(Role.class)));
    return fields;
  }
}
