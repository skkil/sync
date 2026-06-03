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

    GetReflectionResponse.Project project =
        GetReflectionResponse.Project.builder().id(1L).name("Project Name").build();

    return GetReflectionResponse.builder()
        .id(1L)
        .slug("test-slug")
        .author(author)
        .project(project)
        .content("Reflection Content")
        .likeCount(1L)
        .commentCount(1L)
        .bookmarked(true)
        .build();
  }

  public static ResponseFieldsSnippet getReflectionResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("Reflection ID"),
        fieldWithPath("slug").type(JsonFieldType.STRING).description("Reflection Slug"),
        fieldWithPath("author").type(JsonFieldType.OBJECT).description("Author Information"),
        fieldWithPath("author.id").type(JsonFieldType.NUMBER).description("Author User ID"),
        fieldWithPath("author.name").type(JsonFieldType.STRING).description("Author Name"),
        fieldWithPath("project").type(JsonFieldType.OBJECT).description("Project Information"),
        fieldWithPath("project.id").type(JsonFieldType.NUMBER).description("Project ID"),
        fieldWithPath("project.name").type(JsonFieldType.STRING).description("Project Name"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("Reflection Content"),
        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("Number of Likes"),
        fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("Number of Comments"),
        fieldWithPath("bookmarked")
            .type(JsonFieldType.BOOLEAN)
            .description("Whether the current user bookmarked this reflection"));
  }
}
