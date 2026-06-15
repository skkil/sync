package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.post.dto.response.GetPostResponse;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostResponseSnippets {

  public static GetPostResponse getGetPostResponse() {
    GetPostResponse.Author author =
        GetPostResponse.Author.builder().id(1L).name("Author Name").build();

    GetPostResponse.Project project =
        GetPostResponse.Project.builder().id(1L).name("Project Name").build();

    return GetPostResponse.builder()
        .id(1L)
        .slug("test-slug")
        .author(author)
        .project(project)
        .content("Post Content")
        .likeCount(1L)
        .commentCount(1L)
        .bookmarked(true)
        .build();
  }

  public static ResponseFieldsSnippet getPostResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("Post ID"),
        fieldWithPath("slug").type(JsonFieldType.STRING).description("Post Slug"),
        fieldWithPath("author").type(JsonFieldType.OBJECT).description("Author Information"),
        fieldWithPath("author.id").type(JsonFieldType.NUMBER).description("Author User ID"),
        fieldWithPath("author.name").type(JsonFieldType.STRING).description("Author Name"),
        fieldWithPath("project").type(JsonFieldType.OBJECT).description("Project Information"),
        fieldWithPath("project.id").type(JsonFieldType.NUMBER).description("Project ID"),
        fieldWithPath("project.name").type(JsonFieldType.STRING).description("Project Name"),
        fieldWithPath("content").type(JsonFieldType.STRING).description("Post Content"),
        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("Number of Likes"),
        fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("Number of Comments"),
        fieldWithPath("bookmarked")
            .type(JsonFieldType.BOOLEAN)
            .description("Whether the current user bookmarked this post"));
  }
}
