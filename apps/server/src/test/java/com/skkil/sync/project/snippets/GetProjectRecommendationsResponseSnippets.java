package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.GetProjectRecommendationsResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectRecommendationsResponseSnippets {

  public static GetProjectRecommendationsResponse getGetProjectRecommendationsResponse() {
    return new GetProjectRecommendationsResponse(
        List.of(ProjectSummarySnippets.getProjectSummary()));
  }

  public static ResponseFieldsSnippet getGetProjectRecommendationsResponseFields() {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath("projects").type(JsonFieldType.ARRAY).description("추천 프로젝트 목록"));
    fields.addAll(ProjectSummarySnippets.getProjectSummaryFields("projects[]."));

    return responseFields(fields.toArray(new FieldDescriptor[0]));
  }
}
