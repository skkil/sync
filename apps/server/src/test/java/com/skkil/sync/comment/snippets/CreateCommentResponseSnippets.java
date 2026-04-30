package com.skkil.sync.comment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.comment.dto.response.CreateCommentResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CreateCommentResponseSnippets {

  public static CreateCommentResponse getCreateCommentResponse() {
    return new CreateCommentResponse(1L);
  }

  public static ResponseFieldsSnippet getCreateCommentResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("Created comment ID"));
  }
}
