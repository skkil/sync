package com.skkil.sync.comment.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.comment.dto.request.CreateCommentRequest;
import com.skkil.sync.comment.enums.CommentTargetType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateCommentRequestSnippets {

  public static CreateCommentRequest getCreateCommentRequest() {
    return new CreateCommentRequest(CommentTargetType.REFLECTION, 1L, null, "Comment content");
  }

  public static RequestFieldsSnippet getCreateCommentRequestFields() {
    return requestFields(
        fieldWithPath("targetType").type(JsonFieldType.STRING).description("Comment target type"),
        fieldWithPath("targetId").type(JsonFieldType.NUMBER).description("Comment target ID"),
        fieldWithPath("parentId")
            .type(JsonFieldType.NUMBER)
            .description("Parent comment ID")
            .optional(),
        fieldWithPath("content").type(JsonFieldType.STRING).description("Comment content"));
  }
}
