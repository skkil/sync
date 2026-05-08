package com.skkil.sync.like.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.like.dto.response.CreateLikeResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CreateLikeResponseSnippets {

  public static CreateLikeResponse getCreateLikeResponse() {
    return new CreateLikeResponse(1L, true);
  }

  public static ResponseFieldsSnippet getCreateLikeResponseFields() {
    return responseFields(
        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("Like count"),
        fieldWithPath("liked").type(JsonFieldType.BOOLEAN).description("Liked flag"));
  }
}
