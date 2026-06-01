package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.project.dto.response.CreateProjectResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CreateProjectResponseSnippets {

  public static CreateProjectResponse getCreateProjectResponse() {
    return new CreateProjectResponse(1L);
  }

  public static ResponseFieldsSnippet getCreateProjectResponseFields() {
    return responseFields(
        fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("생성된 프로젝트 ID"));
  }
}
