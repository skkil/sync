package com.skkil.sync.project.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.project.dto.request.AddTeammateRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class AddTeammateRequestSnippets {

  public static AddTeammateRequest getAddTeammateRequest() {
    return new AddTeammateRequest("john-doe");
  }

  public static RequestFieldsSnippet getAddTeammateRequestFields() {
    return requestFields(
        fieldWithPath("teammateHandle").type(JsonFieldType.STRING).description("추가할 팀원의 핸들"));
  }
}
