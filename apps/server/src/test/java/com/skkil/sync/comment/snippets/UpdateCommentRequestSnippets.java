package com.skkil.sync.comment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.comment.dto.request.UpdateCommentRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdateCommentRequestSnippets {

  public static UpdateCommentRequest getUpdateCommentRequest() {
    return new UpdateCommentRequest("Updated comment content");
  }

  public static RequestFieldsSnippet getUpdateCommentRequestFields() {
    return requestFields(
        fieldWithPath("content").type(JsonFieldType.STRING).description("Comment content"));
  }
}
