package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.GetProjectTeammatesResponse;
import com.skkil.sync.project.model.Role;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectTeammatesResponseSnippets {

  public static GetProjectTeammatesResponse getGetProjectTeammatesResponse() {
    return new GetProjectTeammatesResponse(
        List.of(
            ProjectTeammateSummarySnippets.getProjectTeammate(Role.ADMIN),
            ProjectTeammateSummarySnippets.getProjectTeammate(Role.MEMBER)));
  }

  public static ResponseFieldsSnippet getGetProjectTeammatesResponseFields() {
    return responseFields(fieldWithPath("teammates").type(JsonFieldType.ARRAY).description("팀원 목록"))
        .and(
            ProjectTeammateSummarySnippets.getProjectTeammateFields("teammates[].")
                .toArray(FieldDescriptor[]::new));
  }
}
