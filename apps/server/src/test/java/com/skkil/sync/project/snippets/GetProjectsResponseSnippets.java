package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.GetProjectsResponse;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetProjectsResponseSnippets {

  public static GetProjectsResponse getGetProjectsResponse() {
    return new GetProjectsResponse(
        List.of(
            new GetProjectsResponse.Project(1L, "spring-boot-project", "Spring Boot 프로젝트"),
            new GetProjectsResponse.Project(2L, "react-project", "React 프로젝트")));
  }

  public static ResponseFieldsSnippet getGetProjectsResponseFields() {
    return responseFields(
        fieldWithPath("projects").type(JsonFieldType.ARRAY).description("프로젝트 목록"),
        fieldWithPath("projects[].id").type(JsonFieldType.NUMBER).description("프로젝트 ID"),
        fieldWithPath("projects[].handle").type(JsonFieldType.STRING).description("프로젝트 핸들"),
        fieldWithPath("projects[].name").type(JsonFieldType.STRING).description("프로젝트 이름"));
  }
}
