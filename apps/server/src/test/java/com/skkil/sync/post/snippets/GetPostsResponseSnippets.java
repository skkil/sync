package com.skkil.sync.post.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import com.skkil.sync.common.util.restdocs.RestDocsUtils;
import com.skkil.sync.common.util.time.DateTimeTestUtils;
import com.skkil.sync.post.dto.response.GetPostsResponse;
import com.skkil.sync.post.model.PostType;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetPostsResponseSnippets {

  public static GetPostsResponse getGetPostsResponse() {
    GetPostsResponse.Author author = new GetPostsResponse.Author(1L, "User");
    GetPostsResponse.Project project = new GetPostsResponse.Project(1L, "Project");

    GetPostsResponse.Post post =
        new GetPostsResponse.Post(
            1L,
            PostType.SHORT,
            author,
            project,
            "Post Content",
            DateTimeTestUtils.defaultTestLocalDateTime());

    return new GetPostsResponse(CursorPaginationResponseSnippets.of(List.of(post)));
  }

  public static ResponseFieldsSnippet getPostsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("posts");

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Post ID"),
            fieldWithPath(".type")
                .type(RestDocsUtils.ENUM_TYPE)
                .description("Post Type")
                .attributes(RestDocsUtils.getEnumAttributes(PostType.class)),
            fieldWithPath(".content").type(JsonFieldType.STRING).description("Post Content"),
            fieldWithPath(".createdAt")
                .type(JsonFieldType.STRING)
                .description("Creation Timestamp"));

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content.author",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Author User ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Author Name"));

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content.project",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Project ID"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Project Name"));

    return responseFields(fields.getFieldDescriptors());
  }
}
