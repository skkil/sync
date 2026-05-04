package com.skkil.sync.reflection.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.reflection.dto.response.GetReflectionResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetReflectionResponseSnippets {

  public static GetReflectionResponse getGetReflectionResponse() {
    GetReflectionResponse.Author author =
        GetReflectionResponse.Author.builder().id(1L).name("Author Name").build();

    return GetReflectionResponse.builder()
        .id(1L)
        .author(author)
        .content("Reflection Content")
        .likeCount(1L)
        .commentCount(1L)
        .build();
  }

  public static ResponseFieldsSnippet getReflectionResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("Reflection ID").optional(),
        fieldWithPath("author").type(JsonFieldType.OBJECT).description("Author Information"),
        fieldWithPath("author.id").type(JsonFieldType.NUMBER).description("Author User ID"),
        fieldWithPath("author.name").type(JsonFieldType.STRING).description("Author Name"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("Reflection Content"),
        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("Number of Likes"),
        fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("Number of Comments"));
  }
}
