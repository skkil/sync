package com.skkil.sync.bookmark.snippets;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

import com.epages.restdocs.apispec.FieldDescriptors;
import com.skkil.sync.bookmark.dto.response.GetBookmarkedReflectionsResponse;
import com.skkil.sync.common.util.pagination.snippets.CursorPaginationResponseSnippets;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;

public class GetBookmarkedReflectionsResponseSnippets {

  public static GetBookmarkedReflectionsResponse getGetBookmarkedReflectionsResponse() {
    GetBookmarkedReflectionsResponse.Author author =
        new GetBookmarkedReflectionsResponse.Author(
            1L, "user_handle", "User Name", "https://example.com/profile.jpg");

    GetBookmarkedReflectionsResponse.Post post =
        new GetBookmarkedReflectionsResponse.Post(
            1L,
            "test-slug",
            author,
            "This is a bookmarked reflection content",
            10L,
            5L,
            true,
            LocalDateTime.of(2026, 5, 4, 12, 0, 0),
            LocalDateTime.of(2026, 5, 5, 12, 0, 0));

    return new GetBookmarkedReflectionsResponse(CursorPaginationResponseSnippets.of(List.of(post)));
  }

  public static ResponseFieldsSnippet getBookmarkedReflectionsResponseFields() {
    FieldDescriptors fields =
        CursorPaginationResponseSnippets.getCursorPaginationResponseFields("posts");

    fields =
        fields.andWithPrefix(
            "posts.nodes[].content",
            fieldWithPath(".id").type(JsonFieldType.NUMBER).description("Reflection ID"),
            fieldWithPath(".slug").type(JsonFieldType.STRING).description("Reflection Slug"),
            fieldWithPath(".content").type(JsonFieldType.STRING).description("Content"),
            fieldWithPath(".likeCount").type(JsonFieldType.NUMBER).description("Like Count"),
            fieldWithPath(".commentCount").type(JsonFieldType.NUMBER).description("Comment Count"),
            fieldWithPath(".bookmarked")
                .type(JsonFieldType.BOOLEAN)
                .description("Whether the current user bookmarked this reflection"),
            fieldWithPath(".createdAt").type(JsonFieldType.STRING).description("Created At"),
            fieldWithPath(".bookmarkedAt").type(JsonFieldType.STRING).description("Bookmarked At"));

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
