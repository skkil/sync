package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.reflection.dto.request.UpdateTagRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdateTagRequestSnippets {

  public static UpdateTagRequest getUpdateTagRequest() {
    return new UpdateTagRequest("업데이트된 태그 설명", true);
  }

  public static RequestFieldsSnippet getUpdateTagRequestFields() {
    return requestFields(
        fieldWithPath("description").type(JsonFieldType.STRING).description("태그 설명").optional(),
        fieldWithPath("verified").type(JsonFieldType.BOOLEAN).description("태그 검증 여부").optional());
  }
}
