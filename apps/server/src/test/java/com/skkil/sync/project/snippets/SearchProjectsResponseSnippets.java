package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.SearchProjectsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class SearchProjectsResponseSnippets {

  public static SearchProjectsResponse getSearchProjectsResponse() {
    return new SearchProjectsResponse(
        List.of(
            new SearchProjectsResponse.Project(1L, "Spring Boot 프로젝트"),
            new SearchProjectsResponse.Project(2L, "React 프로젝트")));
  }

  public static ResponseFieldsSnippet getSearchProjectsResponseFields() {
    return responseFields(
        fieldWithPath("projects").type(JsonFieldType.ARRAY).description("프로젝트 목록"),
        fieldWithPath("projects[].id").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
        fieldWithPath("projects[].name").type(JsonFieldType.STRING).description("프로젝트 이름"));
  }
}
