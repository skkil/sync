package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.project.dto.request.CreateProjectRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateProjectRequestSnippets {

  public static CreateProjectRequest getCreateProjectRequest() {
    return new CreateProjectRequest("새로운 프로젝트");
  }

  public static RequestFieldsSnippet getCreateProjectRequestFields() {
    return requestFields(fieldWithPath("name").type(JsonFieldType.STRING).description("프로젝트 이름"));
  }
}
