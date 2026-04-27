package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.reflection.dto.request.CreateReflectionRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateReflectionRequestSnippets {

  public static CreateReflectionRequest getCreateReflectionRequest() {
    return new CreateReflectionRequest("title", "This is a reflection content.");
  }

  public static RequestFieldsSnippet getCreateReflectionRequestFields() {
    return requestFields(
        fieldWithPath("title").type(JsonFieldType.STRING).description("Title").optional(),
        fieldWithPath("content").type(JsonFieldType.STRING).description("Content"));
  }
}
