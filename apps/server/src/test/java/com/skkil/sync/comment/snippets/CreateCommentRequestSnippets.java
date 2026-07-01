package com.skkil.sync.comment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateCommentRequestSnippets {

  public static CreateCommentRequest getCreateCommentRequest() {
    return new CreateCommentRequest("Comment content");
  }

  public static RequestFieldsSnippet getCreateCommentRequestFields() {
    return requestFields(
        fieldWithPath("content").type(JsonFieldType.STRING).description("Comment content"));
  }
}
