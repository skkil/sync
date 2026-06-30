package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.CreatePostResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CreatePostResponseSnippets {

  public static CreatePostResponse getCreatePostResponse() {
    return new CreatePostResponse("slug");
  }

  public static ResponseFieldsSnippet getCreatePostResponseFields() {
    return responseFields(
        fieldWithPath("slug").type(JsonFieldType.STRING).description("Post Slug"));
  }
}
