package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.post.dto.request.UpdateTagRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdateTagRequestSnippets {

  public static UpdateTagRequest getUpdateTagRequest() {
    return new UpdateTagRequest("kotlin", "코틀린 관련 태그");
  }

  public static RequestFieldsSnippet getUpdateTagRequestFields() {
    return requestFields(
        fieldWithPath("name").type(JsonFieldType.STRING).description("변경할 태그 이름"),
        fieldWithPath("description")
            .type(JsonFieldType.STRING)
            .description("변경할 태그 설명")
            .optional());
  }
}
