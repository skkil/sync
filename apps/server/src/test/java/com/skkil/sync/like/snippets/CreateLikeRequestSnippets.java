package com.skkil.sync.like.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;

import com.skkil.sync.like.dto.request.CreateLikeRequest;
import com.skkil.sync.like.enums.LikeTargetType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.RequestFieldsSnippet;

public class CreateLikeRequestSnippets {

  public static CreateLikeRequest getCreateLikeRequest() {
    return new CreateLikeRequest(LikeTargetType.REFLECTION, 1L);
  }

  public static RequestFieldsSnippet getCreateLikeRequestFields() {
    return requestFields(
        fieldWithPath("targetType").type(JsonFieldType.STRING).description("Like target type"),
        fieldWithPath("targetId").type(JsonFieldType.NUMBER).description("Like target ID"));
  }
}
