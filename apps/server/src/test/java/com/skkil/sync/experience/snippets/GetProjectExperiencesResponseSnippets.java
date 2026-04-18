package com.skkil.sync.experience.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.experience.dto.response.GetProjectExperiencesResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectExperiencesResponseSnippets {

  public static GetProjectExperiencesResponse getGetProjectExperiencesResponse() {
    GetProjectExperiencesResponse.ProjectExperience experience =
        new GetProjectExperiencesResponse.ProjectExperience(1L, 100L, "Sample Project Name");

    return new GetProjectExperiencesResponse(List.of(experience));
  }

  public static ResponseFieldsSnippet getGetProjectExperiencesResponseFieldsSnippet() {
    FieldDescriptors fields =
        new FieldDescriptors(
            fieldWithPath("experiences")
                .type(JsonFieldType.ARRAY)
                .description("List of project experiences"),
            fieldWithPath("experiences[].id")
                .type(JsonFieldType.NUMBER)
                .description("Experience ID"),
            fieldWithPath("experiences[].projectId")
                .type(JsonFieldType.NUMBER)
                .description("Project ID"),
            fieldWithPath("experiences[].projectName")
                .type(JsonFieldType.STRING)
                .description("Project Name"));

    return responseFields(fields.getFieldDescriptors());
  }
}
