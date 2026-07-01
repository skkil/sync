package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.post.dto.request.UpdatePostRequest;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class UpdatePostRequestSnippets {

  public static UpdatePostRequest getUpdatePostRequest() {
    return new UpdatePostRequest("This is a post content.");
  }

  public static RequestFieldsSnippet getUpdatePostRequestFields() {
    return requestFields(
        fieldWithPath("content").type(JsonFieldType.STRING).description("Content"));
  }
}
