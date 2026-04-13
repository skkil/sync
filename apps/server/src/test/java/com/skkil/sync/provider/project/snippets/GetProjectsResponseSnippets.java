package com.skkil.sync.provider.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.dto.response.CursorPaginationResponse;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.provider.project.dto.response.GetProjectsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectsResponseSnippets {

  public static GetProjectsResponse getGetProjectsResponse() {
    GetProjectsResponse.Project project =
        new GetProjectsResponse.Project(1L, "Project Name", "Project Description");

    return new GetProjectsResponse(
        new CursorPaginationResponse<>(List.of(project), true, "next-cursor"));
  }

  public static ResponseFieldsSnippet getProjectsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("projects");

    fields =
        fields.andWithPrefix(
            "projects.content[]",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Project ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Project Name"),
            fieldWithPath(".description")
                .type(JsonFieldType.STRING)
                .description("Project Description"));

    return responseFields(fields.getFieldDescriptors());
  }
}
