package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.post.dto.request.CreateTagRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateTagRequestSnippets {

  public static CreateTagRequest getCreateTagRequest() {
    return new CreateTagRequest("java");
  }

  public static RequestFieldsSnippet getCreateTagRequestFields() {
    return requestFields(fieldWithPath("name").type(JsonFieldType.STRING).description("태그 이름"));
  }
}
