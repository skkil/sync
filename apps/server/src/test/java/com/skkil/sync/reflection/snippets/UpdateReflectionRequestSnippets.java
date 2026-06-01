package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.reflection.dto.request.UpdateReflectionRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdateReflectionRequestSnippets {

  public static UpdateReflectionRequest getUpdateReflectionRequest() {
    return new UpdateReflectionRequest(1L, "This is a reflection content.");
  }

  public static RequestFieldsSnippet getUpdateReflectionRequestFields() {
    return requestFields(
        fieldWithPath("projectId").type(JsonFieldType.NUMBER).description("Project ID").optional(),
        fieldWithPath("content").type(JsonFieldType.STRING).description("Content"));
  }
}
