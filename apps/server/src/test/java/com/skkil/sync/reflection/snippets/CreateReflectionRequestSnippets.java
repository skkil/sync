package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.reflection.dto.request.CreateReflectionRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateReflectionRequestSnippets {

  public static CreateReflectionRequest getCreateReflectionRequest() {
    CreateReflectionRequest.Content content =
        new CreateReflectionRequest.Content(
            "This is a reflection content.", "{\"text\": \"This is a reflection content.\"}");

    return new CreateReflectionRequest("title", content);
  }

  public static RequestFieldsSnippet getCreateReflectionRequestFields() {
    return requestFields(
        fieldWithPath("title").type(JsonFieldType.STRING).description("Title").optional(),
        fieldWithPath("content").type(JsonFieldType.OBJECT).description("Content"),
        fieldWithPath("content.text").type(JsonFieldType.STRING).description("Text Content"),
        fieldWithPath("content.json").type(JsonFieldType.STRING).description("JSON Content"));
  }
}
