package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.CreatePostTemplateResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CreatePostTemplateResponseSnippets {

  public static CreatePostTemplateResponse getCreatePostTemplateResponse() {
    return new CreatePostTemplateResponse("template-external-id");
  }

  public static ResponseFieldsSnippet getCreatePostTemplateResponseFields() {
    return responseFields(
        fieldWithPath("externalId").type(JsonFieldType.STRING).description("생성된 템플릿의 외부 식별자"));
  }
}
