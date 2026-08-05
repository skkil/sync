package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.GetMyProjectsResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetMyProjectsResponseSnippets {

  public static GetMyProjectsResponse getGetMyProjectsResponse() {
    return new GetMyProjectsResponse(List.of(MyProjectSummarySnippets.getMyProjectSummary()));
  }

  public static ResponseFieldsSnippet getGetMyProjectsResponseFields() {
    List<FieldDescriptor> fields = new ArrayList<>();
    fields.add(fieldWithPath("projects").type(JsonFieldType.ARRAY).description("내 프로젝트 목록"));
    fields.addAll(MyProjectSummarySnippets.getMyProjectSummaryFields("projects[]."));

    return responseFields(fields.toArray(new FieldDescriptor[0]));
  }
}
