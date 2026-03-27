package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.reflection.dto.response.CreateReflectionResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CreateReflectionResponseSnippets {

  public static CreateReflectionResponse getCreateReflectionResponse() {
    return new CreateReflectionResponse(1L);
  }

  public static ResponseFieldsSnippet getCreateReflectionResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("Reflection ID"));
  }
}
