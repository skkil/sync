package com.skkil.sync.bookmark.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.bookmark.dto.response.GetBookmarkedPostsResponse;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetBookmarkedPostsResponseSnippets {

  public static GetBookmarkedPostsResponse getGetBookmarkedPostsResponse() {
    GetBookmarkedPostsResponse.Author author =
        new GetBookmarkedPostsResponse.Author(
            1L, "user_handle", "User Name", "https://example.com/profile.jpg");

    GetBookmarkedPostsResponse.Post post =
        new GetBookmarkedPostsResponse.Post(
            1L,
            "test-slug",
            author,
            "This is a bookmarked post content",
            10L,
            5L,
            true,
            OffsetDateTime.of(2026, 5, 4, 12, 0, 0, 0, ZoneOffset.UTC),
            OffsetDateTime.of(2026, 5, 5, 12, 0, 0, 0, ZoneOffset.UTC));

    return new GetBookmarkedPostsResponse(CursorPaginationResponseSnippets.of(List.of(post)));
  }

  public static ResponseFieldsSnippet getBookmarkedPostsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("posts");

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Post ID"),
            fieldWithPath(".slug").type(JsonFieldType.STRING).description("Post Slug"),
            fieldWithPath(".content").type(JsonFieldType.STRING).description("Content"),
            fieldWithPath(".likeCount").type(JsonFieldType.NUMBER).description("Like Count"),
            fieldWithPath(".commentCount").type(JsonFieldType.NUMBER).description("Comment Count"),
            fieldWithPath(".bookmarked")
                .type(JsonFieldType.BOOLEAN)
                .description("Whether the current user bookmarked this post"),
            fieldWithPath(".createdAt").type(JsonFieldType.STRING).description("Created At"),
            fieldWithPath(".bookmarkedAt").type(JsonFieldType.STRING).description("Bookmarked At"),
            fieldWithPath(".author").type(JsonFieldType.OBJECT).description("Author Information"));

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content.author",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Author ID"),
            fieldWithPath(".handle").type(JsonFieldType.STRING).description("Author Handle"),
            fieldWithPath(".name").type(JsonFieldType.STRING).description("Author Name"),
            fieldWithPath(".profileImageUrl")
                .type(JsonFieldType.STRING)
                .description("Author Profile Image URL")
                .optional());

    return responseFields(fields.getFieldDescriptors());
  }
}
