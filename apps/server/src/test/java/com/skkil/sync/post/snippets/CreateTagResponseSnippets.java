package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.CreateTagResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class CreateTagResponseSnippets {

  public static CreateTagResponse getCreateTagResponse() {
    return new CreateTagResponse(1L, "java", "", 0L);
  }

  public static ResponseFieldsSnippet getCreateTagResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 태그 ID"),
        fieldWithPath("name").type(JsonFieldType.STRING).description("태그 이름"),
        fieldWithPath("description").type(JsonFieldType.STRING).description("태그 설명"),
        fieldWithPath("postCount").type(JsonFieldType.NUMBER).description("태그가 사용된 게시물 수"));
  }
}
