package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.common.util.time.DateTimeTestUtils;
import com.skkil.sync.post.dto.response.GetPostResponse;
import com.skkil.sync.post.model.PostType;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostResponseSnippets {

  public static GetPostResponse getGetPostResponse() {
    GetPostResponse.Author author =
        GetPostResponse.Author.builder().name("Author Name").handle("author-handle").build();

    GetPostResponse.Project project =
        GetPostResponse.Project.builder().handle("project-handle").name("Project Name").build();

    GetPostResponse.Media media =
        GetPostResponse.Media.builder().id(1L).url("https://example.com/media.png").build();

    GetPostResponse.Content content =
        GetPostResponse.Content.builder().json("Post Content").media(List.of(media)).build();

    return GetPostResponse.builder()
        .id(1L)
        .type(PostType.SHORT)
        .slug("test-slug")
        .author(author)
        .project(project)
        .content(content)
        .likeCount(1L)
        .commentCount(1L)
        .bookmarked(true)
        .createdAt(DateTimeTestUtils.defaultTestOffsetDateTime())
        .build();
  }

  public static ResponseFieldsSnippet getPostResponseFields() {
    return responseFields(
        fieldWithPath("id").type(JsonFieldType.NUMBER).description("Post ID"),
        fieldWithPath("type")
            .type(RestDocsUtils.ENUM_TYPE)
            .description("Post Type")
            .attributes(RestDocsUtils.getEnumAttributes(PostType.class)),
        fieldWithPath("slug").type(JsonFieldType.STRING).description("Post Slug"),
        fieldWithPath("author").type(JsonFieldType.OBJECT).description("Author Information"),
        fieldWithPath("author.name").type(JsonFieldType.STRING).description("Author Name"),
        fieldWithPath("author.handle").type(JsonFieldType.STRING).description("Author Handle"),
        fieldWithPath("project")
            .type(JsonFieldType.OBJECT)
            .description("Project Information")
            .optional(),
        fieldWithPath("project.handle").type(JsonFieldType.STRING).description("Project Handle"),
        fieldWithPath("project.name").type(JsonFieldType.STRING).description("Project Name"),
        fieldWithPath("content").type(JsonFieldType.OBJECT).description("Post Content"),
        fieldWithPath("content.json").type(JsonFieldType.STRING).description("Post Content JSON"),
        fieldWithPath("content.media")
            .type(JsonFieldType.ARRAY)
            .description("Media attached to the post"),
        fieldWithPath("content.media[].id").type(JsonFieldType.NUMBER).description("Media ID"),
        fieldWithPath("content.media[].url").type(JsonFieldType.STRING).description("Media URL"),
        fieldWithPath("likeCount").type(JsonFieldType.NUMBER).description("Number of Likes"),
        fieldWithPath("commentCount").type(JsonFieldType.NUMBER).description("Number of Comments"),
        fieldWithPath("bookmarked")
            .type(JsonFieldType.BOOLEAN)
            .description("Whether the current user bookmarked this post"),
        fieldWithPath("createdAt").type(JsonFieldType.STRING).description("Creation Timestamp"));
  }
}
